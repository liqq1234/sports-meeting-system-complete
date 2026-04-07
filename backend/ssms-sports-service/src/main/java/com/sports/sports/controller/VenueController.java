package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Venue;
import com.sports.sports.service.VenueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "比赛场地管理")
@RestController
@RequestMapping("/venue")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @ApiOperation("分页查询场地")
    @GetMapping("/page")
    public Result<PageResult<Venue>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        return Result.success(venueService.getPage(pageNum, pageSize, name, type, status));
    }

    @ApiOperation("根据ID查询场地")
    @GetMapping("/{id}")
    public Result<Venue> getById(@PathVariable Long id) {
        return Result.success(venueService.getById(id));
    }

    @ApiOperation("获取所有可用场地列表")
    @GetMapping("/list")
    public Result<List<Venue>> listAll() {
        return Result.success(venueService.listAll());
    }

    @ApiOperation("新增场地")
    @PostMapping
    public Result<?> add(@RequestBody Venue venue) {
        venueService.add(venue);
        return Result.success("新增成功");
    }

    @ApiOperation("修改场地")
    @PutMapping
    public Result<?> update(@RequestBody Venue venue) {
        venueService.update(venue);
        return Result.success("修改成功");
    }

    @ApiOperation("删除场地")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        venueService.delete(id);
        return Result.success("删除成功");
    }
}
