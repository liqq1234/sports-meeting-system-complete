package com.sports.logistics.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sports.logistics.common.PageResult;
import com.sports.logistics.common.Result;
import com.sports.logistics.entity.Material;
import com.sports.logistics.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后勤物资管理")
@RestController
@RequestMapping("/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @ApiOperation("分页查询物资")
    @GetMapping("/page")
    public Result<PageResult<Material>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        return Result.success(PageResult.build(materialService.getMaterialList(new Page<>(pageNum, pageSize), name)));
    }

    @ApiOperation("根据ID查询物资")
    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Long id) {
        return Result.success(materialService.getById(id));
    }

    @ApiOperation("新增物资")
    @PostMapping
    public Result<?> add(@RequestBody Material material) {
        materialService.save(material);
        return Result.success("新增成功");
    }

    @ApiOperation("修改物资")
    @PutMapping
    public Result<?> update(@RequestBody Material material) {
        materialService.updateById(material);
        return Result.success("修改成功");
    }

    @ApiOperation("删除物资")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return Result.success("删除成功");
    }
}
