package com.sports.auth.controller;

import com.sports.auth.common.PageResult;
import com.sports.auth.common.Result;
import com.sports.auth.entity.User;
import com.sports.auth.service.UserService;
import com.sports.auth.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<User>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) Integer status) {
        return Result.success(userService.getPage(pageNum, pageSize, username, realName, role, college, status));
    }

    @ApiOperation("根据ID查询用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation("新增用户（管理员）")
    @PostMapping
    public Result<?> add(@RequestBody User user) {
        userService.add(user);
        return Result.success("新增成功");
    }

    @ApiOperation("修改用户（管理员）")
    @PutMapping
    public Result<?> update(@RequestBody User user) {
        userService.update(user);
        return Result.success("修改成功");
    }

    @ApiOperation("删除用户（管理员）")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }

    @ApiOperation("重置密码（管理员）")
    @PutMapping("/resetPassword/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("操作成功");
    }

    @ApiOperation("修改个人信息")
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        user.setId(UserContext.getCurrentUserId());
        userService.updateProfile(user);
        return Result.success("修改成功");
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            return Result.error("请填写完整信息");
        }
        userService.changePassword(UserContext.getCurrentUserId(), oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
