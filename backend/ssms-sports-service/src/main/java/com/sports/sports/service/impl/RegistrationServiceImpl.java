package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.Constants;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.*;
import com.sports.sports.mapper.*;
import com.sports.sports.service.MessageService;
import com.sports.sports.service.RegistrationService;
import com.sports.sports.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public PageResult<Registration> getPage(Integer pageNum, Integer pageSize, Long meetingId, Long eventId, Long userId, Integer status, String keyword) {
        Page<Registration> page = new Page<>(pageNum, pageSize);
        IPage<Registration> result = registrationMapper.selectRegistrationPage(page, meetingId, eventId, userId, status, keyword);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    @Transactional
    public void enroll(Long eventId, String remark) {
        Long userId = UserContext.getCurrentUserId();

        // 检查比赛项目是否存在
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new RuntimeException("比赛项目不存在");
        }

        // 检查运动会是否在报名中
        Meeting meeting = meetingMapper.selectById(event.getMeetingId());
        if (meeting == null || meeting.getStatus() != Constants.MEETING_ENROLLING) {
            throw new RuntimeException("当前运动会不在报名阶段");
        }

        // 检查报名时间
        LocalDateTime now = LocalDateTime.now();
        if (meeting.getEnrollStart() != null && now.isBefore(meeting.getEnrollStart())) {
            throw new RuntimeException("报名尚未开始");
        }
        if (meeting.getEnrollEnd() != null && now.isAfter(meeting.getEnrollEnd())) {
            throw new RuntimeException("报名已截止");
        }

        // 检查性别限制
        Integer currentUserGender = UserContext.getCurrentUserGender();
        if (event.getGenderLimit() != null && event.getGenderLimit() != 2) {
            if (!event.getGenderLimit().equals(currentUserGender)) {
                throw new RuntimeException("该项目有性别限制，不符合报名条件");
            }
        }

        // 检查是否已报名
        Long existCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .eq(Registration::getEventId, eventId)
                        .ne(Registration::getStatus, Constants.REG_CANCELLED));
        if (existCount > 0) {
            throw new RuntimeException("您已报名该项目，请勿重复报名");
        }

        // 检查该运动会报名项目数量是否超过3个
        Long enrolledCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getUserId, userId)
                        .eq(Registration::getMeetingId, event.getMeetingId())
                        .in(Registration::getStatus, Constants.REG_PENDING, Constants.REG_APPROVED));
        if (enrolledCount >= 3) {
            throw new RuntimeException("每位运动员每届运动会最多报名3个项目");
        }

        // 检查参赛人数是否已满
        if (event.getMaxParticipants() > 0) {
            Integer currentCount = registrationMapper.countByEventId(eventId);
            if (currentCount >= event.getMaxParticipants()) {
                throw new RuntimeException("该项目报名人数已满");
            }
        }

        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setEventId(eventId);
        registration.setMeetingId(event.getMeetingId());
        registration.setStatus(Constants.REG_PENDING);
        registration.setRemark(remark);
        registrationMapper.insert(registration);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new RuntimeException("报名记录不存在");
        }

        Long userId = UserContext.getCurrentUserId();
        Integer role = UserContext.getCurrentUserRole();
        if (role != Constants.ROLE_ADMIN && !reg.getUserId().equals(userId)) {
            throw new RuntimeException("只能取消自己的报名");
        }
        if (reg.getStatus() == Constants.REG_CANCELLED) {
            throw new RuntimeException("该报名已取消");
        }

        reg.setStatus(Constants.REG_CANCELLED);
        registrationMapper.updateById(reg);
    }

    @Override
    @Transactional
    public void review(Long id, Integer status, String rejectReason) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new RuntimeException("报名记录不存在");
        }
        if (reg.getStatus() != Constants.REG_PENDING) {
            throw new RuntimeException("该报名记录不在待审核状态");
        }

        reg.setStatus(status);
        reg.setReviewedBy(UserContext.getCurrentUserId());
        reg.setReviewTime(LocalDateTime.now());
        if (status == Constants.REG_REJECTED) {
            reg.setRejectReason(rejectReason);
        }
        registrationMapper.updateById(reg);

        // 发送消息通知运动员
        String title = status == Constants.REG_APPROVED ? "报名审核通过" : "报名审核被驳回";
        String content = status == Constants.REG_APPROVED
                ? "您的报名申请已通过审核，请按时参赛。"
                : "您的报名申请被驳回，原因：" + (rejectReason != null ? rejectReason : "未说明");

        Message message = new Message();
        message.setUserId(reg.getUserId());
        message.setTitle(title);
        message.setContent(content);
        message.setType(1);
        message.setRelatedId(id);
        messageService.send(message);
    }

    @Override
    @Transactional
    public void batchReview(List<Long> ids, Integer status, String rejectReason) {
        for (Long id : ids) {
            review(id, status, rejectReason);
        }
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
        return registrationMapper.selectCollegeRegistrationStats(meetingId);
    }
}
