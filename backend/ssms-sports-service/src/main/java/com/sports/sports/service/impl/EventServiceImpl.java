package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Event;
import com.sports.sports.mapper.EventMapper;
import com.sports.sports.service.EventService;
import com.sports.sports.service.RegistrationService;
import com.sports.sports.service.ScheduleService;
import com.sports.sports.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ScoreService scoreService;

    @Override
    public PageResult<Event> getPage(Integer pageNum, Integer pageSize, Long meetingId, String name, String category, Integer genderLimit, Integer status) {
        Page<Event> page = new Page<>(pageNum, pageSize);
        IPage<Event> result = eventMapper.selectEventPage(page, meetingId, name, category, genderLimit, status);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public Event getById(Long id) {
        Event event = eventMapper.selectEventDetail(id);
        if (event == null) {
            throw new RuntimeException("比赛项目不存在");
        }
        return event;
    }

    @Override
    public List<Event> listByMeetingId(Long meetingId) {
        return eventMapper.selectList(
                new LambdaQueryWrapper<Event>()
                        .eq(Event::getMeetingId, meetingId)
                        .orderByAsc(Event::getEventDate)
                        .orderByAsc(Event::getStartTime));
    }

    @Override
    public void add(Event event) {
        eventMapper.insert(event);
    }

    @Override
    public void update(Event event) {
        eventMapper.updateById(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 先删除相关的成绩记录
        scoreService.deleteByEventId(id);
        // 删除相关的赛程安排
        scheduleService.deleteByEventId(id);
        // 删除相关的报名记录
        registrationService.deleteByEventId(id);
        // 最后删除比赛项目
        eventMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByMeetingId(Long meetingId) {
        // 先删除相关的成绩记录
        scoreService.deleteByMeetingId(meetingId);
        // 删除相关的赛程安排
        scheduleService.deleteByMeetingId(meetingId);
        // 删除相关的报名记录
        registrationService.deleteByMeetingId(meetingId);
        // 最后删除比赛项目
        eventMapper.delete(new LambdaQueryWrapper<Event>().eq(Event::getMeetingId, meetingId));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Event event = new Event();
        event.setId(id);
        event.setStatus(status);
        eventMapper.updateById(event);
    }

    @Override
    public List<Map<String, Object>> getEventStats(Long meetingId) {
        return eventMapper.selectEventStats(meetingId);
    }
}
