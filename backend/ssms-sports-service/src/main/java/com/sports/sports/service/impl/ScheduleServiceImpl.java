package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.Constants;
import com.sports.sports.common.PageResult;
import com.sports.sports.common.exception.BusinessException;
import com.sports.sports.entity.*;
import com.sports.sports.mapper.*;
import com.sports.sports.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;

    private final ScheduleAthleteMapper scheduleAthleteMapper;

    private final EventMapper eventMapper;

    private final RegistrationMapper registrationMapper;
    private final VenueMapper venueMapper;
    private final ScoreMapper scoreMapper;
    private final com.sports.sports.client.UserClient userClient;

    @Override
    @Cacheable(value = "schedules", key = "#meetingId + '_' + #eventId + '_' + #pageNum + '_' + #pageSize + '_' + #status")
    public PageResult<Schedule> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, LocalDate eventDate, Integer status, Long refereeId) {
        Page<Schedule> page = new Page<>(pageNum, pageSize);
        IPage<Schedule> result = scheduleMapper.selectSchedulePage(page, meetingId, eventId, eventDate, status, refereeId);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    @Cacheable(value = "schedule:detail", key = "#id")
    public Schedule getById(Long id) {
        Schedule schedule = scheduleMapper.selectScheduleDetail(id);
        if (schedule == null) {
            throw new BusinessException("赛程不存在");
        }
        return schedule;
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void add(Schedule schedule) {
        log.info("新增赛程接收到的数据: {}", schedule);
        if (schedule.getVenueId() != null) {
            Venue venue = venueMapper.selectById(schedule.getVenueId());
            if (venue != null) {
                schedule.setVenueName(venue.getName());
            }
        }
        scheduleMapper.insert(schedule);
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void update(Schedule schedule) {
        if (schedule.getVenueId() != null) {
            Venue venue = venueMapper.selectById(schedule.getVenueId());
            if (venue != null) {
                schedule.setVenueName(venue.getName());
            }
        }
        scheduleMapper.updateById(schedule);
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void delete(Long id) {
        scheduleMapper.deleteById(id);
        // 同时删除赛程运动员关联
        scheduleAthleteMapper.delete(
                new LambdaQueryWrapper<ScheduleAthlete>().eq(ScheduleAthlete::getScheduleId, id));
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void deleteByEventId(Long eventId) {
        // 先获取该项目的所有赛程ID
        List<Schedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>().eq(Schedule::getEventId, eventId));
        for (Schedule schedule : schedules) {
            // 删除赛程运动员关联
            scheduleAthleteMapper.delete(
                    new LambdaQueryWrapper<ScheduleAthlete>().eq(ScheduleAthlete::getScheduleId, schedule.getId()));
        }
        // 删除赛程
        scheduleMapper.delete(new LambdaQueryWrapper<Schedule>().eq(Schedule::getEventId, eventId));
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void deleteByMeetingId(Long meetingId) {
        // 先获取该运动会的所有赛程ID
        List<Schedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>().eq(Schedule::getMeetingId, meetingId));
        for (Schedule schedule : schedules) {
            // 删除赛程运动员关联
            scheduleAthleteMapper.delete(
                    new LambdaQueryWrapper<ScheduleAthlete>().eq(ScheduleAthlete::getScheduleId, schedule.getId()));
        }
        // 删除赛程
        scheduleMapper.delete(new LambdaQueryWrapper<Schedule>().eq(Schedule::getMeetingId, meetingId));
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void updateStatus(Long id, Integer status) {
        Schedule schedule = new Schedule();
        schedule.setId(id);
        schedule.setStatus(status);
        scheduleMapper.updateById(schedule);
    }



    @Override
    public List<ScheduleAthlete> getAthletes(Long scheduleId) {
        List<ScheduleAthlete> athletes = scheduleAthleteMapper.selectByScheduleId(scheduleId);
        if (athletes != null && !athletes.isEmpty()) {
            java.util.List<Long> userIds = athletes.stream()
                .map(ScheduleAthlete::getUserId)
                .collect(java.util.stream.Collectors.toList());
            
            com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
            if (userResult.getCode() == 200 && userResult.getData() != null) {
                java.util.Map<Long, com.sports.sports.client.vo.UserVO> userMap = userResult.getData().stream()
                    .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u));
                
                for (ScheduleAthlete sa : athletes) {
                    com.sports.sports.client.vo.UserVO vo = userMap.get(sa.getUserId());
                    if (vo != null) {
                        sa.setUserRealName(vo.getRealName());
                        sa.setUserCollege(vo.getCollege());
                        sa.setUserClassName(vo.getClassName());
                    }
                }
            }
        }
        return athletes;
    }

    @Override
    @Transactional
    public void assignAthletes(Long scheduleId, List<Long> userIds) {
        // 获取当前赛程信息
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("赛程不存在");
        }

        // 1. 检查重复分配：同一个项目，一个运动员只能参加一个赛程
        // 获取该项目的所有赛程ID
        List<Long> allScheduleIds = scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>().eq(Schedule::getEventId, schedule.getEventId())
        ).stream().map(Schedule::getId).collect(Collectors.toList());

        // 检查是否有运动员已经分配到了该项目的其他赛程
        List<Long> otherScheduleIds = allScheduleIds.stream()
                .filter(id -> !id.equals(scheduleId))
                .collect(Collectors.toList());
        
        if (!otherScheduleIds.isEmpty()) {
            List<ScheduleAthlete> existingAssignments = scheduleAthleteMapper.selectList(
                    new LambdaQueryWrapper<ScheduleAthlete>()
                            .in(ScheduleAthlete::getScheduleId, otherScheduleIds)
                            .in(ScheduleAthlete::getUserId, userIds));
            
            if (!existingAssignments.isEmpty()) {
                throw new BusinessException("分配失败：部分运动员已分配到该项目的其他赛程中，不可重复分配");
            }
        }

        // 2. 清理当前赛程的原有分配和待录入成绩
        scheduleAthleteMapper.delete(
                new LambdaQueryWrapper<ScheduleAthlete>().eq(ScheduleAthlete::getScheduleId, scheduleId));
        scoreMapper.delete(new LambdaQueryWrapper<Score>()
                .eq(Score::getScheduleId, scheduleId)
                .eq(Score::getStatus, Constants.SCORE_PENDING));

        // 3. 执行新分配
        int laneNo = 1;
        for (Long userId : userIds) {
            // 插入赛程运动员关联
            ScheduleAthlete sa = new ScheduleAthlete();
            sa.setScheduleId(scheduleId);
            sa.setUserId(userId);
            sa.setLaneNo(laneNo++);
            sa.setStatus(0);
            scheduleAthleteMapper.insert(sa);

            // 4. 初始化成绩记录（如果该项目下该运动员还没有成绩记录）
            Score existingScore = scoreMapper.selectOne(new LambdaQueryWrapper<Score>()
                    .eq(Score::getEventId, schedule.getEventId())
                    .eq(Score::getUserId, userId));
            
            if (existingScore == null) {
                Score score = new Score();
                score.setUserId(userId);
                score.setEventId(schedule.getEventId());
                score.setMeetingId(schedule.getMeetingId());
                score.setScheduleId(scheduleId);
                score.setStatus(Constants.SCORE_PENDING);
                scoreMapper.insert(score);
            }
        }
    }
}
