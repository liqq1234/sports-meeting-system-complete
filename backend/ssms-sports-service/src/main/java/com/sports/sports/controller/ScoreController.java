package com.sports.sports.controller;

import com.sports.sports.common.PageResult;
import com.sports.sports.common.Result;
import com.sports.sports.entity.Score;
import com.sports.sports.service.ScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "成绩管理")
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @ApiOperation("分页查询成绩")
    @GetMapping("/page")
    public Result<PageResult<Score>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(scoreService.getPage(pageNum, pageSize, meetingId, eventId, userId, status, keyword));
    }

    @ApiOperation("查询我的成绩记录")
    @GetMapping("/my")
    public Result<PageResult<Score>> getMyScores(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long meetingId) {
        Long userId = com.sports.sports.util.UserContext.getCurrentUserId();
        return Result.success(scoreService.getPage(pageNum, pageSize, meetingId, null, userId, null, null));
    }

    @ApiOperation("登分/录入成绩")
    @PostMapping("/record")
    public Result<?> record(@RequestBody Score score) {
        scoreService.record(score);
        return Result.success("成绩录入成功");
    }

    @ApiOperation("批量录入成绩")
    @PostMapping("/batchRecord")
    public Result<?> batchRecord(@RequestBody List<Score> scores) {
        scoreService.batchRecord(scores);
        return Result.success("批量录入成功");
    }

    @ApiOperation("确认成绩")
    @PutMapping("/confirm/{id}")
    public Result<?> confirm(@PathVariable Long id) {
        scoreService.confirm(id);
        return Result.success("成绩已确认");
    }

    @ApiOperation("公布成绩")
    @PutMapping("/publish/{eventId}")
    public Result<?> publish(@PathVariable Long eventId) {
        scoreService.publish(eventId);
        return Result.success("成绩已公布并通知运动员");
    }

    @ApiOperation("获取项目成绩排行")
    @GetMapping("/list/{eventId}")
    public Result<List<Score>> listEventScores(@PathVariable Long eventId) {
        return Result.success(scoreService.getEventScores(eventId));
    }

    @ApiOperation("运动会积分分布快照")
    @GetMapping("/stats/distribution/{meetingId}")
    public Result<List<Map<String, Object>>> getScoreDistribution(@PathVariable Long meetingId) {
        return Result.success(scoreService.getScoreDistribution(meetingId));
    }

    @ApiOperation("学院积分排名")
    @GetMapping("/stats/collegeRanking/{meetingId}")
    public Result<List<Map<String, Object>>> getCollegeRanking(@PathVariable Long meetingId) {
        return Result.success(scoreService.getCollegeRanking(meetingId));
    }

    @ApiOperation("优秀运动员排名")
    @GetMapping("/stats/topAthletes/{meetingId}")
    public Result<List<Map<String, Object>>> getTopAthletes(@PathVariable Long meetingId, @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(scoreService.getTopAthletes(meetingId, limit));
    }
}
