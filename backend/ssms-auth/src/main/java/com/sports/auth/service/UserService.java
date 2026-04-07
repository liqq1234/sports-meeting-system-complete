package com.sports.auth.service;

import com.sports.auth.common.PageResult;
import com.sports.auth.entity.User;

public interface UserService {

    PageResult<User> getPage(Integer pageNum, Integer pageSize, String username, String realName, Integer role, String college, Integer status);

    User getById(Long id);

    void add(User user);

    void update(User user);

    void delete(Long id);

    void resetPassword(Long id);

    void updateStatus(Long id, Integer status);

    void updateProfile(User user);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
