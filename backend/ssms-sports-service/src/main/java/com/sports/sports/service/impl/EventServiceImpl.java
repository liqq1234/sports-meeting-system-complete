package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Event;
import com.sports.sports.mapper.EventMapper;
import com.sports.sports.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

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
    public void delete(Long id) {
        eventMapper.deleteById(id);
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
