package com.sports.sports.service;

import com.sports.sports.common.PageResult;
import com.sports.sports.entity.Message;

public interface MessageService {
    PageResult<Message> getPage(Integer pageNum, Integer pageSize, Long userId, Integer type, Integer isRead);
    Integer countUnread(Long userId);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
    void send(Message message);
    void delete(Long id);
}
