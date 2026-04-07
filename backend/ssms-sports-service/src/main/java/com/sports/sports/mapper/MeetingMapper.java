package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {

    IPage<Meeting> selectMeetingPage(Page<Meeting> page,
                                     @Param("name") String name,
                                     @Param("status") Integer status);
}
