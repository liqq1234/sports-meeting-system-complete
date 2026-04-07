package com.sports.medical.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.medical.common.PageResult;
import com.sports.medical.common.Result;
import com.sports.medical.entity.MedicalRecord;
import com.sports.medical.service.MedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "医疗记录管理")
@RestController
@RequestMapping("/record")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @ApiOperation("分页查询医疗记录")
    @GetMapping("/page")
    public Result<PageResult<MedicalRecord>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Long patientId) {
        return Result.success(PageResult.build(medicalRecordService.getRecordList(new Page<>(pageNum, pageSize), meetingId, patientId)));
    }

    @ApiOperation("新增医疗记录")
    @PostMapping
    public Result<?> add(@RequestBody MedicalRecord record) {
        medicalRecordService.save(record);
        return Result.success("新增成功");
    }

    @ApiOperation("修改医疗记录")
    @PutMapping
    public Result<?> update(@RequestBody MedicalRecord record) {
        medicalRecordService.updateById(record);
        return Result.success("修改成功");
    }

    @ApiOperation("删除医疗记录")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        medicalRecordService.removeById(id);
        return Result.success("删除成功");
    }
}
