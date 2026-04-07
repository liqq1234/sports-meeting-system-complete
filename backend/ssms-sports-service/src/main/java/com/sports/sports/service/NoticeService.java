package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Notice;

public interface NoticeService {
    PageResult<Notice> getPage(Integer pageNum, Integer pageSize, Long meetingId, Integer type, String keyword, Integer targetRole);
    Notice getById(Long id);
    void add(Notice notice);
    void update(Notice notice);
    void delete(Long id);
    void publish(Long id);
}
