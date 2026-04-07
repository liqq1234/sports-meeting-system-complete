package com.sports.sports.controller;

import com.sports.sports.common.Result;
import com.sports.sports.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "数据统计分析")
@RestController
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取运动会详情看板数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@RequestParam Long meetingId) {
        return Result.success(statisticsService.getDashboard(meetingId));
    }

    @ApiOperation("获取系统概览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    @ApiOperation("获取医疗保障分析")
    @GetMapping("/medical")
    public Result<Map<String, Object>> getMedicalAnalytics(@RequestParam Long meetingId) {
        return Result.success(statisticsService.getMedicalAnalytics(meetingId));
    }
}
