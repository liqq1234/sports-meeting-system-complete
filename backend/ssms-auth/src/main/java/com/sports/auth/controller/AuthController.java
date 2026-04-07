package com.sports.auth.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sports.auth.common.Result;
import com.sports.auth.entity.User;
import com.sports.auth.mapper.UserMapper;
import com.sports.auth.util.JwtTokenUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        log.info("用户尝试登录: username='{}', password(length={})='{}'", request.getUsername(), request.getPassword().length(), request.getPassword());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        
        if (user != null) {
            log.info("数据库查询到的Hash: length={}, value='{}'", user.getPassword().length(), user.getPassword());
        }

        if (user != null && cn.hutool.crypto.digest.BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            if (user.getStatus() != 1) {
                log.warn("用户登录失败: 账号已被禁用, username={}", request.getUsername());
                return Result.error("账号已被禁用");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            claims.put("role", user.getRole());
            claims.put("gender", user.getGender());
            
            String token = jwtTokenUtil.generateToken(user.getUsername(), claims);
            log.info("用户登录成功: username={}, role={}", user.getUsername(), user.getRoleName());
            
            Map<String, Object> data = new HashMap<>();
            data.put("access_token", token);
            data.put("token_type", "Bearer");
            data.put("expires_in", 86400);
            data.put("user", user);
            
            return Result.success(data);
        } else {
            log.warn("用户登录失败: 用户名或密码错误, username={}, 数据库中是否存在该用户: {}, 密码是否匹配: {}", 
                     request.getUsername(), 
                     user != null, 
                     user != null && BCrypt.checkpw(request.getPassword(), user.getPassword()));
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())) != null) {
            return Result.error("用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setStatus(1);
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    @GetMapping("/info")
    public Result<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtTokenUtil.getUsernameFromToken(token.substring(7));
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.success("退出成功");
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }
}
