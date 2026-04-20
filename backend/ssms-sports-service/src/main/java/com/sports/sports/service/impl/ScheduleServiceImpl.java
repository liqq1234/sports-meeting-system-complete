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
        scheduleMapper.insert(schedule);
    }

    @Override
    @CacheEvict(value = {"schedules", "schedule:detail"}, allEntries = true)
    public void update(Schedule schedule) {
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
    @Transactional
    public void autoGenerate(Long meetingId) {
        // 获取该运动会所有比赛项目
        List<Event> events = eventMapper.selectList(
                new LambdaQueryWrapper<Event>()
                        .eq(Event::getMeetingId, meetingId)
                        .orderByAsc(Event::getEventDate)
                        .orderByAsc(Event::getStartTime));

        for (Event event : events) {
            // 检查是否已有赛程
            Long existCount = scheduleMapper.selectCount(
                    new LambdaQueryWrapper<Schedule>()
                            .eq(Schedule::getEventId, event.getId())
                            .eq(Schedule::getMeetingId, meetingId));
            if (existCount > 0) {
                continue;
            }

            // 获取该项目已通过审核的报名运动员
            List<Registration> registrations = registrationMapper.selectList(
                    new LambdaQueryWrapper<Registration>()
                            .eq(Registration::getEventId, event.getId())
                            .eq(Registration::getStatus, Constants.REG_APPROVED));

            if (registrations.isEmpty()) {
                continue;
            }

            // 创建赛程
            Schedule schedule = new Schedule();
            schedule.setMeetingId(meetingId);
            schedule.setEventId(event.getId());
            schedule.setRound("决赛");
            schedule.setGroupNo(1);
            schedule.setEventDate(event.getEventDate());
            schedule.setStartTime(event.getStartTime());
            schedule.setEndTime(event.getEndTime());
            schedule.setVenueName(event.getVenue());
            schedule.setRefereeId(event.getRefereeId());
            schedule.setStatus(Constants.SCHEDULE_NOT_STARTED);
            scheduleMapper.insert(schedule);

            // 分配运动员到赛程
            int laneNo = 1;
            for (Registration reg : registrations) {
                ScheduleAthlete sa = new ScheduleAthlete();
                sa.setScheduleId(schedule.getId());
                sa.setUserId(reg.getUserId());
                sa.setLaneNo(laneNo++);
                sa.setStatus(0);
                scheduleAthleteMapper.insert(sa);
            }
        }
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
                    }
                }
            }
        }
        return athletes;
    }

    @Override
    @Transactional
    public void assignAthletes(Long scheduleId, List<Long> userIds) {
        // 先删除已有分配
        scheduleAthleteMapper.delete(
                new LambdaQueryWrapper<ScheduleAthlete>().eq(ScheduleAthlete::getScheduleId, scheduleId));

        // 重新分配
        int laneNo = 1;
        for (Long userId : userIds) {
            ScheduleAthlete sa = new ScheduleAthlete();
            sa.setScheduleId(scheduleId);
            sa.setUserId(userId);
            sa.setLaneNo(laneNo++);
            sa.setStatus(0);
            scheduleAthleteMapper.insert(sa);
        }
    }
}
