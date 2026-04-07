package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    IPage<Registration> selectRegistrationPage(Page<Registration> page,
                                               @Param("meetingId") Long meetingId,
                                               @Param("eventId") Long eventId,
                                               @Param("userId") Long userId,
                                               @Param("status") Integer status,
                                               @Param("keyword") String keyword);

    Integer countByEventId(@Param("eventId") Long eventId);

    List<Map<String, Object>> selectRegistrationStats(@Param("meetingId") Long meetingId);

    List<Map<String, Object>> selectCollegeRegistrationStats(@Param("meetingId") Long meetingId);
}
