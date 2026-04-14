package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Registration;
import java.util.List;
import java.util.Map;

public interface RegistrationService {
    PageResult<Registration> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword);
    void enroll(Long eventId, String remark);
    void cancel(Long id);
    void review(Long id, Integer status, String rejectReason);
    void batchReview(List<Long> ids, Integer status, String rejectReason);
    void deleteByEventId(Long eventId);
    void deleteByMeetingId(Long meetingId);
    List<Map<String, Object>> getRegistrationStats(Long meetingId);
    List<Map<String, Object>> getCollegeRegistrationStats(Long meetingId);
}
