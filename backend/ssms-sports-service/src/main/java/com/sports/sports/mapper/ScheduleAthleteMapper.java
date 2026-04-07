package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.sports.entity.ScheduleAthlete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleAthleteMapper extends BaseMapper<ScheduleAthlete> {

    List<ScheduleAthlete> selectByScheduleId(@Param("scheduleId") Long scheduleId);
}
