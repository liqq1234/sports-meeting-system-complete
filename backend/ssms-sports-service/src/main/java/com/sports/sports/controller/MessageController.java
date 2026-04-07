package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Message;
import com.sports.sports.service.MessageService;
import com.sports.sports.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "站内信管理")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("分页查询我的消息")
    @GetMapping("/page")
    public Result<PageResult<Message>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead) {
        return Result.success(messageService.getPage(pageNum, pageSize, UserContext.getCurrentUserId(), type, isRead));
    }

    @ApiOperation("获取未读消息数")
    @GetMapping("/unreadCount")
    public Result<Integer> unreadCount() {
        return Result.success(messageService.countUnread(UserContext.getCurrentUserId()));
    }

    @ApiOperation("标记已读")
    @PutMapping("/markAsRead/{id}")
    public Result<?> markAsRead(@PathVariable Long id) {
        messageService.markAsRead(id);
        return Result.success("操作成功");
    }

    @ApiOperation("标记全部已读")
    @PutMapping("/markAllAsRead")
    public Result<?> markAllAsRead() {
        messageService.markAllAsRead(UserContext.getCurrentUserId());
        return Result.success("操作成功");
    }

    @ApiOperation("删除消息")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        messageService.delete(id);
        return Result.success("删除成功");
    }
}
