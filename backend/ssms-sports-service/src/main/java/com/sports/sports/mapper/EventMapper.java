package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventMapper extends BaseMapper<Event> {

    IPage<Event> selectEventPage(Page<Event> page,
                                 @Param("meetingId") Long meetingId,
                                 @Param("name") String name,
                                 @Param("category") String category,
                                 @Param("genderLimit") Integer genderLimit,
                                 @Param("status") Integer status);

    Event selectEventDetail(@Param("id") Long id);

    List<Map<String, Object>> selectEventStats(@Param("meetingId") Long meetingId);
}
