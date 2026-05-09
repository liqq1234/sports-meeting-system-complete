package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.Constants;
import com.sports.sports.common.PageResult;
import com.sports.sports.common.exception.BusinessException;
import com.sports.sports.entity.Event;
import com.sports.sports.entity.Message;
import com.sports.sports.entity.Score;
import com.sports.sports.mapper.EventMapper;
import com.sports.sports.mapper.ScoreMapper;
import com.sports.sports.service.MessageService;
import com.sports.sports.service.ScoreService;
import com.sports.sports.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;

    private final EventMapper eventMapper;

    private final MessageService messageService;

    private final com.sports.sports.client.UserClient userClient;

    // 积分规则：第1-8名分别获得的积分
    private static final int[] POINT_RULES = {9, 7, 6, 5, 4, 3, 2, 1};

    @Override
    public PageResult<Score> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword) {
        Page<Score> page = new Page<>(pageNum, pageSize);
        // Note: keyword search across services would require fetching IDs from auth-service first.
        IPage<Score> result = scoreMapper.selectScorePage(page, meetingId, eventId, userId, status, null);
        List<Score> records = result.getRecords();
        
        if (records != null && !records.isEmpty()) {
            java.util.Set<Long> userIds = new java.util.HashSet<>();
            for (Score s : records) {
                if (s.getUserId() != null) userIds.add(s.getUserId());
                if (s.getRecordedBy() != null) userIds.add(s.getRecordedBy());
            }
            
            com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(new java.util.ArrayList<>(userIds));
            if (userResult.getCode() == 200 && userResult.getData() != null) {
                java.util.Map<Long, com.sports.sports.client.vo.UserVO> userMap = userResult.getData().stream()
                    .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u));
                    
                for (Score s : records) {
                    com.sports.sports.client.vo.UserVO athlete = userMap.get(s.getUserId());
                    if (athlete != null) {
                        s.setUserName(athlete.getUsername());
                        s.setAthleteName(athlete.getRealName());
                        s.setAthleteCollege(athlete.getCollege());
                    }
                    com.sports.sports.client.vo.UserVO recorder = userMap.get(s.getRecordedBy());
                    if (recorder != null) {
                        s.setRecorderName(recorder.getRealName());
                    }
                }
            }
        }
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), records);
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
        Score existing = null;
        if (score.getId() != null) {
            existing = scoreMapper.selectById(score.getId());
        }
        
        if (existing == null && score.getUserId() != null && score.getEventId() != null) {
            existing = scoreMapper.selectOne(
                    new LambdaQueryWrapper<Score>()
                            .eq(Score::getUserId, score.getUserId())
                            .eq(Score::getEventId, score.getEventId()));
        }

        if (existing != null) {
            // 更新已有成绩
            existing.setScoreValue(score.getScoreValue());
            existing.setScoreText(score.getScoreText());
            existing.setRemark(score.getRemark());
            existing.setStatus(Constants.SCORE_RECORDED);
            existing.setRecordedBy(UserContext.getCurrentUserId());
            scoreMapper.updateById(existing);
        } else {
            // 插入新记录，确保必要字段不为空
            if (score.getUserId() == null || score.getEventId() == null) {
                throw new BusinessException("录入成绩失败：缺少运动员ID或项目ID");
            }
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
            throw new BusinessException("成绩记录不存在");
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

        // 获取项目信息以取得单位
        Event event = eventMapper.selectById(eventId);
        String unit = (event != null && event.getScoreUnit() != null) ? event.getScoreUnit() : "";

        // 更新所有已确认成绩为已公布
        List<Score> scores = scoreMapper.selectList(
                new LambdaQueryWrapper<Score>()
                        .eq(Score::getEventId, eventId)
                        .ge(Score::getStatus, Constants.SCORE_CONFIRMED));
        for (Score score : scores) {
            score.setStatus(Constants.SCORE_PUBLISHED);
            scoreMapper.updateById(score);

            // 确定展示文本
            String scoreDisplay = score.getScoreText();
            if (scoreDisplay == null && score.getScoreValue() != null) {
                // 格式化数值，去掉多余的0并拼接单位
                scoreDisplay = score.getScoreValue().stripTrailingZeros().toPlainString() + unit;
            }

            // 发送成绩通知
            Message msg = new Message();
            msg.setUserId(score.getUserId());
            msg.setTitle("成绩公布通知");
            msg.setContent("您的比赛成绩已公布，成绩：" + (scoreDisplay != null ? scoreDisplay : "无")
                    + "，排名：第" + score.getRanking() + "名"
                    + (score.getPoints() > 0 ? "，获得" + score.getPoints() + "积分" : ""));
            msg.setType(2);
            msg.setRelatedId(score.getId());
            messageService.send(msg);
        }
    }

    @Override
    public List<Score> getEventScores(Long eventId) {
        List<Score> scores = scoreMapper.selectEventScores(eventId);
        if (scores != null && !scores.isEmpty()) {
            java.util.List<Long> userIds = scores.stream().map(Score::getUserId).collect(java.util.stream.Collectors.toList());
            com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
            if (userResult.getCode() == 200 && userResult.getData() != null) {
                java.util.Map<Long, com.sports.sports.client.vo.UserVO> userMap = userResult.getData().stream()
                    .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u));
                for (Score s : scores) {
                    com.sports.sports.client.vo.UserVO vo = userMap.get(s.getUserId());
                    if (vo != null) {
                        s.setAthleteName(vo.getRealName());
                        s.setAthleteCollege(vo.getCollege());
                    }
                }
            }
        }
        return scores;
    }

    @Override
    @Transactional
    public void calculateRanking(Long eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new BusinessException("比赛项目不存在");
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
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void delete(Long id) {
        scoreMapper.deleteById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void deleteByEventId(Long eventId) {
        scoreMapper.delete(new LambdaQueryWrapper<Score>().eq(Score::getEventId, eventId));
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "score:distribution", allEntries = true),
        @CacheEvict(value = "score:collegeRanking", allEntries = true),
        @CacheEvict(value = "score:topAthletes", allEntries = true)
    })
    public void deleteByMeetingId(Long meetingId) {
        scoreMapper.delete(new LambdaQueryWrapper<Score>().eq(Score::getMeetingId, meetingId));
    }

    @Override
    @Cacheable(value = "score:distribution", key = "#meetingId")
    public List<Map<String, Object>> getScoreDistribution(Long meetingId) {
        return scoreMapper.selectScoreDistribution(meetingId);
    }

    @Override
    @Cacheable(value = "score:collegeRanking", key = "#meetingId")
    public List<Map<String, Object>> getCollegeRanking(Long meetingId) {
        List<Map<String, Object>> userPoints = scoreMapper.selectCollegeRanking(meetingId);
        if (userPoints == null || userPoints.isEmpty()) return java.util.Collections.emptyList();
        
        java.util.List<Long> userIds = userPoints.stream()
            .map(m -> {
                Object id = m.get("user_id");
                if (id == null) id = m.get("USER_ID");
                return id instanceof Number ? ((Number) id).longValue() : null;
            })
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());
        
        if (userIds.isEmpty()) return java.util.Collections.emptyList();
        com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
        
        if (userResult.getCode() == 200 && userResult.getData() != null) {
            java.util.Map<Long, String> userCollegeMap = userResult.getData().stream()
                .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u.getCollege() != null ? u.getCollege() : "未知学院"));
            
            java.util.Map<String, Map<String, Object>> collegeStats = new java.util.HashMap<>();
            for (Map<String, Object> up : userPoints) {
                Object uidObj = up.get("user_id");
                if (uidObj == null) uidObj = up.get("USER_ID");
                Long uid = uidObj instanceof Number ? ((Number) uidObj).longValue() : null;
                
                String college = userCollegeMap.getOrDefault(uid, "未知学院");
                Map<String, Object> stat = collegeStats.computeIfAbsent(college, k -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("college", k);
                    m.put("total_points", 0L);
                    m.put("gold", 0L);
                    m.put("silver", 0L);
                    m.put("bronze", 0L);
                    m.put("athlete_count", 0L);
                    return m;
                });
                
                // 安全地从 Map 中提取并转换数值
                long totalPoints = up.get("total_points") instanceof Number ? ((Number) up.get("total_points")).longValue() : 0L;
                long gold = up.get("gold") instanceof Number ? ((Number) up.get("gold")).longValue() : 0L;
                long silver = up.get("silver") instanceof Number ? ((Number) up.get("silver")).longValue() : 0L;
                long bronze = up.get("bronze") instanceof Number ? ((Number) up.get("bronze")).longValue() : 0L;

                stat.put("total_points", (Long) stat.get("total_points") + totalPoints);
                stat.put("gold", (Long) stat.get("gold") + gold);
                stat.put("silver", (Long) stat.get("silver") + silver);
                stat.put("bronze", (Long) stat.get("bronze") + bronze);
                stat.put("athlete_count", (Long) stat.get("athlete_count") + 1);
            }
            return collegeStats.values().stream()
                .sorted((m1, m2) -> {
                    int c = ((Long) m2.get("total_points")).compareTo((Long) m1.get("total_points"));
                    if (c != 0) return c;
                    return ((Long) m2.get("gold")).compareTo((Long) m1.get("gold"));
                })
                .collect(java.util.stream.Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Override
    @Cacheable(value = "score:topAthletes", key = "#meetingId + '_' + #limit")
    public List<Map<String, Object>> getTopAthletes(Long meetingId, Integer limit) {
        List<Map<String, Object>> topUsers = scoreMapper.selectTopAthletes(meetingId, limit != null ? limit : 10);
        if (topUsers == null || topUsers.isEmpty()) return java.util.Collections.emptyList();
        
        java.util.List<Long> userIds = topUsers.stream()
            .map(m -> {
                Object id = m.get("user_id");
                if (id == null) id = m.get("USER_ID");
                return id instanceof Number ? ((Number) id).longValue() : null;
            })
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());
        
        if (userIds.isEmpty()) return java.util.Collections.emptyList();
        com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
        
        if (userResult.getCode() == 200 && userResult.getData() != null) {
            java.util.Map<Long, com.sports.sports.client.vo.UserVO> userMap = userResult.getData().stream()
                .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u));
            
            for (Map<String, Object> tu : topUsers) {
                Object uidObj = tu.get("user_id");
                if (uidObj == null) uidObj = tu.get("USER_ID");
                Long uid = uidObj instanceof Number ? ((Number) uidObj).longValue() : null;
                
                com.sports.sports.client.vo.UserVO vo = userMap.get(uid);
                if (vo != null) {
                    tu.put("real_name", vo.getRealName());
                    tu.put("college", vo.getCollege());
                    tu.put("class_name", vo.getClassName());
                }
            }
        }
        return topUsers;
    }
}
