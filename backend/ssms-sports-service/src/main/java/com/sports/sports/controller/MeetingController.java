package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Meeting;
import com.sports.sports.service.MeetingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "运动会管理")
@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @ApiOperation("分页查询运动会")
    @GetMapping("/page")
    public Result<PageResult<Meeting>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return Result.success(meetingService.getPage(pageNum, pageSize, name, status));
    }

    @ApiOperation("查询所有运动会列表")
    @GetMapping("/list")
    public Result<List<Meeting>> listAll() {
        return Result.success(meetingService.listAll());
    }

    @ApiOperation("根据ID查询运动会")
    @GetMapping("/{id}")
    public Result<Meeting> getById(@PathVariable Long id) {
        return Result.success(meetingService.getById(id));
    }

    @ApiOperation("新增运动会")
    @PostMapping
    public Result<?> add(@RequestBody Meeting meeting) {
        meetingService.add(meeting);
        return Result.success("新增成功");
    }

    @ApiOperation("修改运动会")
    @PutMapping
    public Result<?> update(@RequestBody Meeting meeting) {
        meetingService.update(meeting);
        return Result.success("修改成功");
    }

    @ApiOperation("删除运动会")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        meetingService.delete(id);
        return Result.success("删除成功");
    }

    @ApiOperation("修改运动会状态")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        meetingService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}
