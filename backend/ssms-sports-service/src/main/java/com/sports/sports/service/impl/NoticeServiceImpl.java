package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Notice;
import com.sports.sports.mapper.NoticeMapper;
import com.sports.sports.service.NoticeService;
import com.sports.sports.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public PageResult<Notice> getPage(Integer pageNum, Integer pageSize, Long meetingId, Integer type, String keyword, Integer targetRole) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        IPage<Notice> result = noticeMapper.selectNoticePage(page, meetingId, type, keyword, targetRole);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public Notice getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("通知不存在");
        }
        return notice;
    }

    @Override
    public void add(Notice notice) {
        notice.setPublishedBy(UserContext.getCurrentUserId());
        noticeMapper.insert(notice);
    }

    @Override
    public void update(Notice notice) {
        noticeMapper.updateById(notice);
    }

    @Override
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public void publish(Long id) {
        Notice notice = new Notice();
        notice.setId(id);
        notice.setStatus(1);
        noticeMapper.updateById(notice);
    }
}
