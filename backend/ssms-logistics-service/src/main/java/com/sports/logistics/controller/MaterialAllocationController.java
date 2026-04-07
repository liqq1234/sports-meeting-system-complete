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

@Api(tags = "物资分配申请")
@RestController
@RequestMapping("/allocation")
public class MaterialAllocationController {

    @Autowired
    private MaterialAllocationService allocationService;

    @ApiOperation("分页查询申请记录")
    @GetMapping("/page")
    public Result<PageResult<MaterialAllocation>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Integer status) {
        return Result.success(PageResult.build(allocationService.getAllocationList(new Page<>(pageNum, pageSize), meetingId, status)));
    }

    @ApiOperation("提交物资申请")
    @PostMapping
    public Result<?> apply(@RequestBody MaterialAllocation allocation) {
        allocation.setApplicantId(UserContext.getCurrentUserId());
        allocation.setStatus(0); // 待审核
        allocationService.save(allocation);
        return Result.success("申请提交成功");
    }

    @ApiOperation("审批物资申请")
    @PutMapping("/approve/{id}")
    public Result<?> approve(@PathVariable Long id, @RequestParam Integer status) {
        if (allocationService.approve(id, status)) {
            return Result.success("审核操作成功");
        }
        return Result.error("审核操作失败");
    }
}
