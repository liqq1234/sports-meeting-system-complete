package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Schedule;
import com.sports.sports.entity.ScheduleAthlete;
import com.sports.sports.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Api(tags = "赛程管理")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("分页查询赛程")
    @GetMapping("/page")
    public Result<PageResult<Schedule>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate eventDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long refereeId) {
        return Result.success(scheduleService.getPage(pageNum, pageSize, meetingId, eventId, eventDate, status, refereeId));
    }

    @ApiOperation("获取赛程详情")
    @GetMapping("/{id}")
    public Result<Schedule> getById(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }

    @ApiOperation("新增赛程")
    @PostMapping
    public Result<?> add(@RequestBody Schedule schedule) {
        scheduleService.add(schedule);
        return Result.success("新增成功");
    }

    @ApiOperation("修改赛程")
    @PutMapping
    public Result<?> update(@RequestBody Schedule schedule) {
        scheduleService.update(schedule);
        return Result.success("修改成功");
    }

    @ApiOperation("删除赛程")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success("删除成功");
    }

    @ApiOperation("修改赛程状态")
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        scheduleService.updateStatus(id, status);
        return Result.success("操作成功");
    }

    @ApiOperation("自动生成赛程")
    @PostMapping("/autoGenerate/{meetingId}")
    public Result<?> autoGenerate(@PathVariable Long meetingId) {
        scheduleService.autoGenerate(meetingId);
        return Result.success("赛程自动生成完成");
    }

    @ApiOperation("获取赛程运动员列表")
    @GetMapping("/athletes/{scheduleId}")
    public Result<List<ScheduleAthlete>> getAthletes(@PathVariable Long scheduleId) {
        return Result.success(scheduleService.getAthletes(scheduleId));
    }

    @ApiOperation("手动分配运动员")
    @PostMapping("/assignAthletes/{scheduleId}")
    public Result<?> assignAthletes(@PathVariable Long scheduleId, @RequestBody List<Long> userIds) {
        scheduleService.assignAthletes(scheduleId, userIds);
        return Result.success("分配成功");
    }
}
