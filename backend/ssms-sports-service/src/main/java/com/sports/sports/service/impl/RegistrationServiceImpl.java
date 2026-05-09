package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.Constants;
import com.sports.sports.common.PageResult;
import com.sports.sports.common.exception.BusinessException;
import com.sports.sports.entity.*;
import com.sports.sports.mapper.*;
import com.sports.sports.service.MessageService;
import com.sports.sports.service.RegistrationService;
import com.sports.sports.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final com.sports.sports.client.UserClient userClient;
    private final EventMapper eventMapper;
    private final MeetingMapper meetingMapper;
    private final MessageService messageService;

    @Override
    public PageResult<Registration> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword) {
        Page<Registration> page = new Page<>(pageNum, pageSize);
        // 调用 Mapper 层的自定义关联查询，确保能查到项目名称 (eventName)
        IPage<Registration> result = registrationMapper.selectRegistrationPage(page, meetingId, eventId, userId, status, keyword);
        List<Registration> records = result.getRecords();

        fillUserInformation(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), records);
    }

    private void fillUserInformation(List<Registration> records) {
        if (records == null || records.isEmpty()) return;

        java.util.Set<Long> userIds = new java.util.HashSet<>();
        for (Registration reg : records) {
            if (reg.getUserId() != null) userIds.add(reg.getUserId());
            if (reg.getReviewedBy() != null) userIds.add(reg.getReviewedBy());
        }

        if (userIds.isEmpty()) return;

        com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult =
                userClient.listByIds(new java.util.ArrayList<>(userIds));

        if (userResult.getCode() != 200 || userResult.getData() == null) return;

        java.util.Map<Long, com.sports.sports.client.vo.UserVO> userMap = userResult.getData().stream()
                .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u));

        for (Registration reg : records) {
            com.sports.sports.client.vo.UserVO athlete = userMap.get(reg.getUserId());
            if (athlete != null) {
                reg.setUserName(athlete.getUsername());
                reg.setUserRealName(athlete.getRealName());
                reg.setUserCollege(athlete.getCollege());
                reg.setUserClassName(athlete.getClassName());
            }

            com.sports.sports.client.vo.UserVO reviewer = userMap.get(reg.getReviewedBy());
            if (reviewer != null) {
                reg.setReviewerName(reviewer.getRealName());
            }
        }
    }

    @Override
    @Transactional
    public void enroll(Long eventId, String remark) {
        Long userId = UserContext.getCurrentUserId();
        Event event = eventMapper.selectById(eventId);
        if (event == null) throw new BusinessException("比赛项目不存在");

        Meeting meeting = meetingMapper.selectById(event.getMeetingId());
        validateMeetingStatus(meeting);
        validateUserConstraints(userId, event);

        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setEventId(eventId);
        registration.setMeetingId(event.getMeetingId());
        registration.setStatus(Constants.REG_PENDING);
        registration.setRemark(remark);
        registrationMapper.insert(registration);
    }

    private void validateMeetingStatus(Meeting meeting) {
        if (meeting == null || meeting.getStatus() != Constants.MEETING_ENROLLING) {
            throw new BusinessException("当前运动会不在报名阶段");
        }

        LocalDateTime now = LocalDateTime.now();
        if (meeting.getEnrollStart() != null && now.isBefore(meeting.getEnrollStart())) {
            throw new BusinessException("报名尚未开始");
        }
        if (meeting.getEnrollEnd() != null && now.isAfter(meeting.getEnrollEnd())) {
            throw new BusinessException("报名已截止");
        }
    }

    private void validateUserConstraints(Long userId, Event event) {
        if (event.getStatus() != null && event.getStatus() != 0) {
            String statusText = event.getStatus() == 1 ? "进行中" : "已结束";
            throw new BusinessException("报名失败：该项目当前处于" + statusText + "状态，已停止报名");
        }

        Integer userGender = UserContext.getCurrentUserGender();
        if (event.getGenderLimit() != null && event.getGenderLimit() != 2) {
            if (!event.getGenderLimit().equals(userGender)) {
                String limitText = event.getGenderLimit() == 0 ? "男" : "女";
                throw new BusinessException("性别不符：该项目仅限" + limitText + "运动员报名");
            }
        }

        Long existCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getEventId, event.getId())
                .ne(Registration::getStatus, Constants.REG_CANCELLED));
        if (existCount > 0) throw new BusinessException("您已报名该项目，请勿重复报名");

        Long enrolledCount = registrationMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getMeetingId, event.getMeetingId())
                .in(Registration::getStatus, Constants.REG_PENDING, Constants.REG_APPROVED));
        if (enrolledCount >= 3) throw new BusinessException("每位运动员每届运动会最多报名3个项目");

        if (event.getMaxParticipants() > 0) {
            Integer currentCount = registrationMapper.countByEventId(event.getId());
            if (currentCount >= event.getMaxParticipants()) {
                throw new BusinessException("该项目报名人数已满");
            }
        }
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) throw new BusinessException("报名记录不存在");

        Long userId = UserContext.getCurrentUserId();
        Integer role = UserContext.getCurrentUserRole();
        if (role != Constants.ROLE_ADMIN && !reg.getUserId().equals(userId)) {
            throw new BusinessException("只能取消自己的报名");
        }
        reg.setStatus(Constants.REG_CANCELLED);
        registrationMapper.updateById(reg);
    }

    @Override
    @Transactional
    public void review(Long id, Integer status, String rejectReason) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) throw new BusinessException("报名记录不存在");
        if (reg.getStatus() != Constants.REG_PENDING) throw new BusinessException("该报名记录不在待审核状态");

        reg.setStatus(status);
        reg.setReviewedBy(UserContext.getCurrentUserId());
        reg.setReviewTime(LocalDateTime.now());
        if (status == Constants.REG_REJECTED) reg.setRejectReason(rejectReason);
        registrationMapper.updateById(reg);

        sendReviewNotification(reg, status, rejectReason);
    }

    private void sendReviewNotification(Registration reg, Integer status, String rejectReason) {
        String title = status == Constants.REG_APPROVED ? "报名审核通过" : "报名审核被驳回";
        String content = status == Constants.REG_APPROVED
                ? "您的报名申请已通过审核，请按时参赛。"
                : "您的报名申请被驳回，原因：" + (rejectReason != null ? rejectReason : "未说明");

        Message message = new Message();
        message.setUserId(reg.getUserId());
        message.setTitle(title);
        message.setContent(content);
        message.setType(1);
        message.setRelatedId(reg.getId());
        messageService.send(message);
    }

    @Override
    @Transactional
    public void batchReview(List<Long> ids, Integer status, String rejectReason) {
        for (Long id : ids) review(id, status, rejectReason);
    }

    @Override
    public void deleteByEventId(Long eventId) {
        registrationMapper.delete(new LambdaQueryWrapper<Registration>().eq(Registration::getEventId, eventId));
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        registrationMapper.delete(new LambdaQueryWrapper<Registration>().eq(Registration::getMeetingId, meetingId));
    }

    @Override
    public List<Map<String, Object>> getRegistrationStats(Long meetingId) {
        return registrationMapper.selectRegistrationStats(meetingId);
    }

    @Override
    public List<Map<String, Object>> getCollegeRegistrationStats(Long meetingId) {
        List<Map<String, Object>> rawStats = registrationMapper.selectCollegeRegistrationStats(meetingId);
        if (rawStats == null || rawStats.isEmpty()) return java.util.Collections.emptyList();

        java.util.List<Long> userIds = rawStats.stream()
            .map(m -> {
                Object uid = m.get("user_id");
                if (uid == null) uid = m.get("USER_ID");
                return uid instanceof Number ? ((Number) uid).longValue() : null;
            })
            .filter(java.util.Objects::nonNull)
            .collect(java.util.stream.Collectors.toList());

        com.sports.sports.common.Result<List<com.sports.sports.client.vo.UserVO>> userResult = userClient.listByIds(userIds);
        if (userResult.getCode() == 200 && userResult.getData() != null) {
            return aggregateAndSortCollegeStats(rawStats, userResult.getData());
        }
        return java.util.Collections.emptyList();
    }

    private List<Map<String, Object>> aggregateAndSortCollegeStats(List<Map<String, Object>> rawStats, List<com.sports.sports.client.vo.UserVO> users) {
        java.util.Map<Long, String> userCollegeMap = users.stream()
                .collect(java.util.stream.Collectors.toMap(com.sports.sports.client.vo.UserVO::getId, u -> u.getCollege() != null ? u.getCollege() : "未知学院"));

        class CollegeData {
            java.util.Set<Long> athleteIds = new java.util.HashSet<>();
            long eventCount = 0;
        }
        java.util.Map<String, CollegeData> collegeDataMap = new java.util.HashMap<>();

        for (Map<String, Object> stat : rawStats) {
            Object userIdObj = stat.get("user_id");
            if (userIdObj == null) userIdObj = stat.get("USER_ID");
            Long userId = userIdObj instanceof Number ? ((Number) userIdObj).longValue() : null;
            Object totalObj = stat.get("total");
            if (totalObj == null) totalObj = stat.get("TOTAL");
            Long total = totalObj instanceof Number ? ((Number) totalObj).longValue() : 0L;
            if (userId == null) continue;
            String college = userCollegeMap.getOrDefault(userId, "未知学院");
            CollegeData data = collegeDataMap.computeIfAbsent(college, k -> new CollegeData());
            data.athleteIds.add(userId);
            data.eventCount += total;
        }

        return collegeDataMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("college", entry.getKey());
                    m.put("athlete_count", (long) entry.getValue().athleteIds.size());
                    m.put("event_count", entry.getValue().eventCount);
                    return m;
                })
                .sorted((m1, m2) -> ((Long) m2.get("event_count")).compareTo((Long) m1.get("event_count")))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void withdrawForMedicalReason(Long userId, Long eventId) {
        Registration reg = registrationMapper.selectOne(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getUserId, userId)
                .eq(Registration::getEventId, eventId)
                .in(Registration::getStatus, Constants.REG_PENDING, Constants.REG_APPROVED));
        
        if (reg != null) {
            reg.setStatus(Constants.REG_MEDICAL_WITHDRAWN);
            registrationMapper.updateById(reg);
            
            Message msg = new Message();
            msg.setUserId(userId);
            msg.setTitle("医疗退赛通知");
            msg.setContent("经医务组判定，您目前的身体状况不建议继续参加该比赛项目，已为您办理医疗退赛。请遵医嘱休息。");
            msg.setType(Constants.NOTICE_SYSTEM);
            messageService.send(msg);
        }
    }

    @Override
    public List<Registration> getApprovedRegistrations(Long userId, Long meetingId) {
        // 为了显示名称，这里还是调用 selectRegistrationPage 更好，但为了简单实现接口：
        Page<Registration> page = new Page<>(1, 100);
        return registrationMapper.selectRegistrationPage(page, meetingId, null, userId, 1, null).getRecords();
    }
}
