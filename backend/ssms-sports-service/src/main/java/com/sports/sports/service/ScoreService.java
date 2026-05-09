package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Score;
import java.util.List;
import java.util.Map;

public interface ScoreService {
    PageResult<Score> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword);
    void record(Score score);
    void batchRecord(List<Score> scores);
    void confirm(Long id);
    void publish(Long eventId);
    List<Score> getEventScores(Long eventId);
    void delete(Long id);
    void deleteByEventId(Long eventId);
    void deleteByMeetingId(Long meetingId);
    List<Map<String, Object>> getScoreDistribution(Long meetingId);
    List<Map<String, Object>> getCollegeRanking(Long meetingId);
    List<Map<String, Object>> getTopAthletes(Long meetingId, Integer limit);
    void calculateRanking(Long eventId);
}
