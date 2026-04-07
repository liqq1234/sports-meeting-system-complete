package com.sports.sports.common;

public class Constants {

    // 角色常量
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_REFEREE = 1;
    public static final int ROLE_ATHLETE = 2;

    // 用户状态
    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_ENABLED = 1;

    // 运动会状态
    public static final int MEETING_PREPARING = 0;
    public static final int MEETING_ENROLLING = 1;
    public static final int MEETING_ENROLL_CLOSED = 2;
    public static final int MEETING_IN_PROGRESS = 3;
    public static final int MEETING_FINISHED = 4;

    // 报名状态
    public static final int REG_PENDING = 0;
    public static final int REG_APPROVED = 1;
    public static final int REG_REJECTED = 2;
    public static final int REG_CANCELLED = 3;

    // 成绩状态
    public static final int SCORE_PENDING = 0;
    public static final int SCORE_RECORDED = 1;
    public static final int SCORE_CONFIRMED = 2;
    public static final int SCORE_PUBLISHED = 3;

    // 赛程状态
    public static final int SCHEDULE_NOT_STARTED = 0;
    public static final int SCHEDULE_IN_PROGRESS = 1;
    public static final int SCHEDULE_COMPLETED = 2;
    public static final int SCHEDULE_CANCELLED = 3;

    // 成绩类型
    public static final int SCORE_TYPE_TIME = 0;      // 计时（越小越好）
    public static final int SCORE_TYPE_DISTANCE = 1;   // 计距/计高（越大越好）
    public static final int SCORE_TYPE_POINT = 2;      // 计分（越大越好）

    // 通知类型
    public static final int NOTICE_SYSTEM = 0;
    public static final int NOTICE_SCHEDULE_CHANGE = 1;
    public static final int NOTICE_REVIEW = 2;
    public static final int NOTICE_SCORE = 3;

    // Redis key前缀
    public static final String REDIS_TOKEN_PREFIX = "ssms:token:";
    public static final String REDIS_USER_PREFIX = "ssms:user:";
    public static final String REDIS_SCHEDULE_PREFIX = "ssms:schedule:";
    public static final String REDIS_SCORE_PREFIX = "ssms:score:";
    public static final String REDIS_EVENT_PREFIX = "ssms:event:";

    // 默认分页参数
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
}
