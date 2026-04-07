package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Schedule;
import com.sports.sports.entity.ScheduleAthlete;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    PageResult<Schedule> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, LocalDate eventDate, Integer status, Long refereeId);
    Schedule getById(Long id);
    void add(Schedule schedule);
    void update(Schedule schedule);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    void autoGenerate(Long meetingId);
    List<ScheduleAthlete> getAthletes(Long scheduleId);
    void assignAthletes(Long scheduleId, List<Long> userIds);
}
