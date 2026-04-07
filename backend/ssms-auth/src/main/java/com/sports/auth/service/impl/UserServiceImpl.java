package com.sports.auth.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.auth.common.Constants;
import com.sports.auth.common.PageResult;
import com.sports.auth.entity.User;
import com.sports.auth.mapper.UserMapper;
import com.sports.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<User> getPage(Integer pageNum, Integer pageSize, String username, String realName, Integer role, String college, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> result = userMapper.selectUserPage(page, username, realName, role, college, status);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), result.getPages(), result.getRecords());
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void add(User user) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword() != null ? user.getPassword() : "123456"));
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        user.setPassword(null);
        user.setUsername(null);
        userMapper.updateById(user);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + user.getId());
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + id);
    }

    @Override
    public void resetPassword(Long id) {
        User user = new User();
        user.setId(id);
        user.setPassword(BCrypt.hashpw("123456"));
        userMapper.updateById(user);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + id);
    }

    @Override
    public void updateProfile(User user) {
        User update = new User();
        update.setId(user.getId());
        update.setRealName(user.getRealName());
        update.setGender(user.getGender());
        update.setPhone(user.getPhone());
        update.setEmail(user.getEmail());
        update.setCollege(user.getCollege());
        update.setClassName(user.getClassName());
        update.setAvatar(user.getAvatar());
        userMapper.updateById(update);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + user.getId());
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        User update = new User();
        update.setId(userId);
        update.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(update);
        redisTemplate.delete(Constants.REDIS_USER_PREFIX + userId);
    }
}
