package com.sports.sports.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    IPage<Score> selectScorePage(Page<Score> page,
                                 @Param("meetingId") Long meetingId,
                                 @Param("eventId") Long eventId,
                                 @Param("userId") Long userId,
                                 @Param("status") Integer status,
                                 @Param("keyword") String keyword);

    List<Score> selectEventScores(@Param("eventId") Long eventId);

    List<Map<String, Object>> selectScoreDistribution(@Param("meetingId") Long meetingId);

    List<Map<String, Object>> selectCollegeRanking(@Param("meetingId") Long meetingId);

    List<Map<String, Object>> selectTopAthletes(@Param("meetingId") Long meetingId, @Param("limit") Integer limit);
}
