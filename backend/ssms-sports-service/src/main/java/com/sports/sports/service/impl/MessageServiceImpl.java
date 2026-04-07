package com.sports.sports.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Message;
import com.sports.sports.mapper.MessageMapper;
import com.sports.sports.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public PageResult<Message> getPage(Integer pageNum, Integer pageSize, Long userId, Integer type, Integer isRead) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);
        if (type != null) {
            wrapper.eq(Message::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }
        wrapper.orderByDesc(Message::getCreateTime);
        IPage<Message> result = messageMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public Integer countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Long id) {
        Message msg = new Message();
        msg.setId(id);
        msg.setIsRead(1);
        messageMapper.updateById(msg);
    }

    @Override
    public void markAllAsRead(Long userId) {
        messageMapper.update(null,
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .eq(Message::getIsRead, 0)
                        .set(Message::getIsRead, 1));
    }

    @Override
    public void send(Message message) {
        message.setIsRead(0);
        messageMapper.insert(message);
    }

    @Override
    public void delete(Long id) {
        messageMapper.deleteById(id);
    }
}
