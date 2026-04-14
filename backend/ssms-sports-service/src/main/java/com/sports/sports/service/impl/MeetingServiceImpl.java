package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Meeting;
import com.sports.sports.mapper.MeetingMapper;
import com.sports.sports.service.EventService;
import com.sports.sports.service.MeetingService;
import com.sports.sports.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private EventService eventService;

    @Override
    public PageResult<Meeting> getPage(Integer pageNum, Integer pageSize, String name, Integer status) {
        Page<Meeting> page = new Page<>(pageNum, pageSize);
        IPage<Meeting> result = meetingMapper.selectMeetingPage(page, name, status);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public Meeting getById(Long id) {
        Meeting meeting = meetingMapper.selectById(id);
        if (meeting == null) {
            throw new RuntimeException("运动会不存在");
        }
        return meeting;
    }

    @Override
    public List<Meeting> listAll() {
        return meetingMapper.selectList(
                new LambdaQueryWrapper<Meeting>().orderByDesc(Meeting::getCreateTime));
    }

    @Override
    public void add(Meeting meeting) {
        meeting.setCreatedBy(UserContext.getCurrentUserId());
        meetingMapper.insert(meeting);
    }

    @Override
    public void update(Meeting meeting) {
        meetingMapper.updateById(meeting);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 先删除该运动会下的所有比赛项目
        eventService.deleteByMeetingId(id);
        // 再删除运动会
        meetingMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setStatus(status);
        meetingMapper.updateById(meeting);
    }
}
