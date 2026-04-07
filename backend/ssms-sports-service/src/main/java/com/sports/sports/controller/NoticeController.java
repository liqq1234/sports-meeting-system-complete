package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Notice;
import com.sports.sports.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "公告通知管理")
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation("分页查询通知")
    @GetMapping("/page")
    public Result<PageResult<Notice>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer targetRole) {
        return Result.success(noticeService.getPage(pageNum, pageSize, meetingId, type, keyword, targetRole));
    }

    @ApiOperation("根据ID查询通知")
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    @ApiOperation("发布新通知")
    @PostMapping
    public Result<?> add(@RequestBody Notice notice) {
        noticeService.add(notice);
        return Result.success("通知添加成功");
    }

    @ApiOperation("修改通知")
    @PutMapping
    public Result<?> update(@RequestBody Notice notice) {
        noticeService.update(notice);
        return Result.success("修改成功");
    }

    @ApiOperation("删除通知")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success("删除成功");
    }

    @ApiOperation("正式发布通知")
    @PutMapping("/publish/{id}")
    public Result<?> publish(@PathVariable Long id) {
        noticeService.publish(id);
        return Result.success("已发布");
    }
}
