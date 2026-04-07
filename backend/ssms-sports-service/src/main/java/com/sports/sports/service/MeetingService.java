package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Meeting;
import java.util.List;

public interface MeetingService {
    PageResult<Meeting> getPage(Integer pageNum, Integer pageSize, String name, Integer status);
    Meeting getById(Long id);
    List<Meeting> listAll();
    void add(Meeting meeting);
    void update(Meeting meeting);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
}
