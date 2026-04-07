package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Event;
import java.util.List;
import java.util.Map;

public interface EventService {
    PageResult<Event> getPage(Integer pageNum, Integer pageSize, Long meetingId, String name, String category, Integer genderLimit, Integer status);
    Event getById(Long id);
    List<Event> listByMeetingId(Long meetingId);
    void add(Event event);
    void update(Event event);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    List<Map<String, Object>> getEventStats(Long meetingId);
}
