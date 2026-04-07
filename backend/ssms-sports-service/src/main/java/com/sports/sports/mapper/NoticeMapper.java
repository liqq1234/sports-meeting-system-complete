package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    IPage<Notice> selectNoticePage(Page<Notice> page,
                                   @Param("meetingId") Long meetingId,
                                   @Param("type") Integer type,
                                   @Param("keyword") String keyword,
                                   @Param("targetRole") Integer targetRole);
}
