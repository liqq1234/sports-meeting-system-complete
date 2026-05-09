package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Registration;
import com.sports.sports.service.RegistrationService;
import com.sports.sports.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "报名管理")
@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @ApiOperation("分页查询报名记录")
    @GetMapping("/page")
    public Result<PageResult<Registration>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(registrationService.getPage(pageNum, pageSize, meetingId, eventId, userId, status, keyword));
    }

    @ApiOperation("查询我的报名记录")
    @GetMapping("/my")
    public Result<PageResult<Registration>> getMyRegistrations(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Integer status) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(registrationService.getPage(pageNum, pageSize, meetingId, null, userId, status, null));
    }

    @ApiOperation("运动员报名")
    @PostMapping("/enroll/{eventId}")
    public Result<?> enroll(@PathVariable Long eventId, @RequestParam(required = false) String remark) {
        registrationService.enroll(eventId, remark);
        return Result.success("报名成功，请等待审核");
    }

    @ApiOperation("取消报名")
    @DeleteMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        registrationService.cancel(id);
        return Result.success("报名已取消");
    }

    @ApiOperation("审核报名申请")
    @PutMapping("/review/{id}")
    public Result<?> review(@PathVariable Long id, @RequestParam Integer status, @RequestParam(required = false) String rejectReason) {
        registrationService.review(id, status, rejectReason);
        return Result.success("审核操作成功");
    }

    @ApiOperation("批量审核报名记录")
    @PutMapping("/batchReview")
    public Result<?> batchReview(@RequestBody Map<String, Object> params) {
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        String rejectReason = (String) params.get("rejectReason");
        registrationService.batchReview(ids, status, rejectReason);
        return Result.success("批量审核完成");
    }

    @ApiOperation("获取报名人数统计")
    @GetMapping("/stats/{meetingId}")
    public Result<List<Map<String, Object>>> getRegistrationStats(@PathVariable Long meetingId) {
        return Result.success(registrationService.getRegistrationStats(meetingId));
    }

    @ApiOperation("获取各学院报名统计")
    @GetMapping("/collegeStats/{meetingId}")
    public Result<List<Map<String, Object>>> getCollegeRegistrationStats(@PathVariable Long meetingId) {
        return Result.success(registrationService.getCollegeRegistrationStats(meetingId));
    }

    @ApiOperation("因伤病强制退赛")
    @PostMapping("/withdraw")
    public Result<?> withdrawForMedicalReason(@RequestParam Long userId, @RequestParam Long eventId) {
        registrationService.withdrawForMedicalReason(userId, eventId);
        return Result.success("该运动员已因伤病成功办理退赛");
    }

    @ApiOperation("获取用户已通过的报名列表")
    @GetMapping("/list/approved")
    public Result<List<Registration>> getApprovedRegistrations(@RequestParam Long userId, @RequestParam Long meetingId) {
        return Result.success(registrationService.getApprovedRegistrations(userId, meetingId));
    }
}
