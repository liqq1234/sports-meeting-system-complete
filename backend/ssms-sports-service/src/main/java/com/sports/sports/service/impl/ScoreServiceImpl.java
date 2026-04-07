package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.Constants;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Event;
import com.sports.sports.entity.Message;
import com.sports.sports.entity.Score;
import com.sports.sports.mapper.EventMapper;
import com.sports.sports.mapper.ScoreMapper;
import com.sports.sports.service.MessageService;
import com.sports.sports.service.ScoreService;
import com.sports.sports.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private MessageService messageService;

    // 积分规则：第1-8名分别获得的积分
    private static final int[] POINT_RULES = {9, 7, 6, 5, 4, 3, 2, 1};

    @Override
    public PageResult<Score> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        IPage<Score> result = scoreMapper.selectScorePage(page, meetingId, eventId, userId, status, keyword);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void record(Score score) {
        // 检查是否已有成绩记录
        Score existing = scoreMapper.selectOne(
                new LambdaQueryWrapper<Score>()
                        .eq(Score::getUserId, score.getUserId())
                        .eq(Score::getEventId, score.getEventId()));
        if (existing != null) {
            // 更新已有成绩
            existing.setScoreValue(score.getScoreValue());
            existing.setScoreText(score.getScoreText());
            existing.setRemark(score.getRemark());
            existing.setStatus(Constants.SCORE_RECORDED);
            existing.setRecordedBy(UserContext.getCurrentUserId());
            scoreMapper.updateById(existing);
        } else {
            score.setStatus(Constants.SCORE_RECORDED);
            score.setRecordedBy(UserContext.getCurrentUserId());
            scoreMapper.insert(score);
        }
    }

    @Override
    @Transactional
    public void batchRecord(List<Score> scores) {
        for (Score score : scores) {
            record(score);
        }
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void confirm(Long id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        score.setStatus(Constants.SCORE_CONFIRMED);
        score.setConfirmedBy(UserContext.getCurrentUserId());
        scoreMapper.updateById(score);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void publish(Long eventId) {
        // 先计算排名
        calculateRanking(eventId);

        // 更新所有已确认成绩为已公布
        List<Score> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<Score>()
                        .eq(Score::getEventId, eventId)
                        .ge(Score::getStatus, Constants.SCORE_CONFIRMED));
        for (Score score : scores) {
            score.setStatus(Constants.SCORE_PUBLISHED);
            scoreMapper.updateById(score);

            // 发送成绩通知
            Message msg = new Message();
            msg.setUserId(score.getUserId());
            msg.setTitle("成绩公布通知");
            msg.setContent("您的比赛成绩已公布，成绩：" + score.getScoreText()
                    + "，排名：第" + score.getRanking() + "名"
                    + (score.getPoints() > 0 ? "，获得" + score.getPoints() + "积分" : ""));
            msg.setType(2);
            msg.setRelatedId(score.getId());
            messageService.send(msg);
        }
    }

    @Override
    public List<Score> getEventScores(Long eventId) {
        return scoreMapper.selectEventScores(eventId);
    }

    @Override
    @Transactional
    public void calculateRanking(Long eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new RuntimeException("比赛项目不存在");
        }

        List<Score> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<Score>()
                        .eq(Score::getEventId, eventId)
                        .ge(Score::getStatus, Constants.SCORE_RECORDED)
                        .isNotNull(Score::getScoreValue));

        if (scores.isEmpty()) return;

        // 根据成绩类型排序
        if (event.getScoreType() == Constants.SCORE_TYPE_TIME) {
            // 计时：越小越好
            scores.sort(Comparator.comparing(Score::getScoreValue));
        } else {
            // 计距/计高/计分：越大越好
            scores.sort(Comparator.comparing(Score::getScoreValue).reversed());
        }

        // 设置排名和积分
        for (int i = 0; i < scores.size(); i++) {
            Score score = scores.get(i);
            score.setRanking(i + 1);
            score.setPoints(i < POINT_RULES.length ? POINT_RULES[i] : 0);
            scoreMapper.updateById(score);
        }
    }

    @Override
    @Cacheable(value = "score:distribution", key = "#meetingId")
    public List<Map<String, Object>> getScoreDistribution(Long meetingId) {
        return scoreMapper.selectScoreDistribution(meetingId);
    }

    @Override
    @Cacheable(value = "score:collegeRanking", key = "#meetingId")
    public List<Map<String, Object>> getCollegeRanking(Long meetingId) {
        return scoreMapper.selectCollegeRanking(meetingId);
    }

    @Override
    @Cacheable(value = "score:topAthletes", key = "#meetingId + '_' + #limit")
    public List<Map<String, Object>> getTopAthletes(Long meetingId, Integer limit) {
        return scoreMapper.selectTopAthletes(meetingId, limit != null ? limit : 10);
    }
}
