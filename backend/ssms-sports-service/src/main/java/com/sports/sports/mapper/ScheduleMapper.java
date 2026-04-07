package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    IPage<Schedule> selectSchedulePage(Page<Schedule> page,
                                       @Param("meetingId") Long meetingId,
                                       @Param("eventId") Long eventId,
                                       @Param("eventDate") LocalDate eventDate,
                                       @Param("status") Integer status,
                                       @Param("refereeId") Long refereeId);

    Schedule selectScheduleDetail(@Param("id") Long id);
}
