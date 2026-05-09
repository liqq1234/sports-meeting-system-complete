package com.sports.logistics.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.logistics.common.PageResult;
import com.sports.logistics.common.Result;
import com.sports.logistics.entity.MaterialAllocation;
import com.sports.logistics.service.MaterialAllocationService;
import com.sports.logistics.util.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "物资分配申请")
@RestController
@RequestMapping("/allocation")
public class MaterialAllocationController {

    @Autowired
    private MaterialAllocationService allocationService;

    @Autowired
    private com.sports.sports.client.SportsServiceClient sportsServiceClient;

    @ApiOperation("分页查询申请记录")
    @GetMapping("/page")
    public Result<PageResult<MaterialAllocation>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long applicantId) {
        return Result.success(PageResult.build(allocationService.getAllocationList(new Page<>(pageNum, pageSize), meetingId, status, applicantId)));
    }

    @ApiOperation("提交物资申请")
    @PostMapping
    public Result<?> apply(@RequestBody MaterialAllocation allocation) {
        try {
            allocation.setApplicantId(UserContext.getCurrentUserId());
            allocation.setStatus(0); // 待审核
            allocationService.save(allocation);
            return Result.success("申请提交成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("审批物资申请")
    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam Integer status) {
        try {
            if (allocationService.approve(id, status)) {
                return Result.success("审核操作成功");
            }
            return Result.error("审核操作失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("归还物资")
    @PutMapping("/return/{id}")
    public Result<?> returnMaterial(@PathVariable Long id) {
        try {
            if (allocationService.returnMaterial(id)) {
                return Result.success("物资归还登记成功");
            }
            return Result.error("物资归还登记失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("查询我的参赛项目")
    @GetMapping("/myEvents")
    public Result<?> getMyEvents(@RequestParam Long meetingId) {
        Long userId = UserContext.getCurrentUserId();
        Integer role = UserContext.getCurrentUserRole();
        
        // 如果是管理员(0)或裁判(1)，返回运动会的所有项目
        if (role != null && (role == 0 || role == 1)) {
            List<com.sports.sports.client.SportsServiceClient.EventDTO> events = sportsServiceClient.getEventsByMeetingId(meetingId).getData();
            if (events != null) {
                return Result.success(events.stream().map(e -> {
                    com.sports.sports.client.SportsServiceClient.RegistrationDTO dto = new com.sports.sports.client.SportsServiceClient.RegistrationDTO();
                    dto.setEventId(e.getId());
                    dto.setEventName(e.getName());
                    return dto;
                }).collect(Collectors.toList()));
            }
        }
        
        // 运动员(2)只看自己通过的报名
        return Result.success(sportsServiceClient.getApprovedRegistrations(userId, meetingId).getData());
    }
}
