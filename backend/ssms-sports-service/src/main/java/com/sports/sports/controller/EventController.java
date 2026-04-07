package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Event;
import com.sports.sports.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "比赛项目管理")
@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @ApiOperation("分页查询比赛项目")
    @GetMapping("/page")
    public Result<PageResult<Event>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer genderLimit,
            @RequestParam(required = false) Integer status) {
        return Result.success(eventService.getPage(pageNum, pageSize, meetingId, name, category, genderLimit, status));
    }

    @ApiOperation("根据ID查询比赛项目")
    @GetMapping("/{id}")
    public Result<Event> getById(@PathVariable Long id) {
        return Result.success(eventService.getById(id));
    }

    @ApiOperation("查询运动会所有项目列表")
    @GetMapping("/list/{meetingId}")
    public Result<List<Event>> listByMeetingId(@PathVariable Long meetingId) {
        return Result.success(eventService.listByMeetingId(meetingId));
    }

    @ApiOperation("新增比赛项目")
    @PostMapping
    public Result<?> add(@RequestBody Event event) {
        eventService.add(event);
        return Result.success("新增成功");
    }

    @ApiOperation("修改比赛项目")
    @PutMapping
    public Result<?> update(@RequestBody Event event) {
        eventService.update(event);
        return Result.success("修改成功");
    }

    @ApiOperation("删除比赛项目")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        eventService.delete(id);
        return Result.success("删除成功");
    }

    @ApiOperation("修改比赛项目状态")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        eventService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    @ApiOperation("获取项目统计信息")
    @GetMapping("/stats/{meetingId}")
    public Result<List<Map<String, Object>>> getEventStats(@PathVariable Long meetingId) {
        return Result.success(eventService.getEventStats(meetingId));
    }
}
