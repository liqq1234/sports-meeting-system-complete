package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sports.sports.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    Integer countUnread(@Param("userId") Long userId);
}
