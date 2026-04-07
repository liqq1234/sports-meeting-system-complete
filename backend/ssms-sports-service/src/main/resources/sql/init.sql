-- ssms_sports database initialization
CREATE DATABASE IF NOT EXISTS ssms_sports DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_sports;

-- 运动会表
DROP TABLE IF EXISTS t_meeting;
CREATE TABLE t_meeting (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '运动会ID',
    name        VARCHAR(200) NOT NULL COMMENT '运动会名称',
    description TEXT         DEFAULT NULL COMMENT '运动会描述',
    start_date  DATE         NOT NULL COMMENT '开始日期',
    end_date    DATE         NOT NULL COMMENT '结束日期',
    location    VARCHAR(200) DEFAULT NULL COMMENT '举办地点',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-筹备中 1-报名中 2-报名截止 3-进行中 4-已结束',
    enroll_start DATETIME    DEFAULT NULL COMMENT '报名开始时间',
    enroll_end   DATETIME    DEFAULT NULL COMMENT '报名截止时间',
    created_by  BIGINT       DEFAULT NULL COMMENT '创建人ID',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动会表';

-- 比赛项目表
DROP TABLE IF EXISTS t_event;
CREATE TABLE t_event (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    meeting_id    BIGINT       NOT NULL COMMENT '所属运动会ID',
    name          VARCHAR(100) NOT NULL COMMENT '项目名称（如100米、跳远）',
    category      VARCHAR(50)  DEFAULT NULL COMMENT '项目分类：田赛/径赛/趣味赛',
    gender_limit  TINYINT      DEFAULT NULL COMMENT '性别限制：0-男 1-女 2-不限',
    max_participants INT       DEFAULT 0 COMMENT '最大参赛人数（0不限）',
    min_participants INT       DEFAULT 0 COMMENT '最少参赛人数',
    event_date    DATE         DEFAULT NULL COMMENT '比赛日期',
    start_time    TIME         DEFAULT NULL COMMENT '比赛开始时间',
    end_time      TIME         DEFAULT NULL COMMENT '比赛结束时间',
    venue         VARCHAR(100) DEFAULT NULL COMMENT '比赛场地',
    score_type    TINYINT      NOT NULL DEFAULT 0 COMMENT '成绩类型：0-计时(越小越好) 1-计距/计高(越大越好) 2-计分(越大越好)',
    score_unit    VARCHAR(20)  DEFAULT NULL COMMENT '成绩单位（秒/米/分）',
    rules         TEXT         DEFAULT NULL COMMENT '比赛规则说明',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未开始 1-进行中 2-已结束',
    referee_id    BIGINT       DEFAULT NULL COMMENT '负责裁判ID',
    deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛项目表';

-- 场地表
DROP TABLE IF EXISTS t_venue;
CREATE TABLE t_venue (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '场地ID',
    name        VARCHAR(100) NOT NULL COMMENT '场地名称',
    location    VARCHAR(200) DEFAULT NULL COMMENT '场地位置',
    capacity    INT          DEFAULT 0 COMMENT '容纳人数',
    type        VARCHAR(50)  DEFAULT NULL COMMENT '场地类型：田径场/游泳池/体育馆',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-维护中 1-可用',
    description TEXT         DEFAULT NULL COMMENT '场地描述',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 报名记录表
DROP TABLE IF EXISTS t_registration;
CREATE TABLE t_registration (
    id            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    user_id       BIGINT   NOT NULL COMMENT '运动员用户ID',
    event_id      BIGINT   NOT NULL COMMENT '比赛项目ID',
    meeting_id    BIGINT   NOT NULL COMMENT '运动会ID',
    status        TINYINT  NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核 1-审核通过 2-审核驳回 3-已取消',
    reject_reason VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
    reviewed_by   BIGINT   DEFAULT NULL COMMENT '审核人ID',
    review_time   DATETIME DEFAULT NULL COMMENT '审核时间',
    remark        VARCHAR(500) DEFAULT NULL COMMENT '报名备注',
    deleted       TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    update_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报名记录表';

-- 赛程安排表
DROP TABLE IF EXISTS t_schedule;
CREATE TABLE t_schedule (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '赛程ID',
    meeting_id  BIGINT       NOT NULL COMMENT '运动会ID',
    event_id    BIGINT       NOT NULL COMMENT '比赛项目ID',
    round       VARCHAR(50)  DEFAULT '决赛' COMMENT '轮次：预赛/半决赛/决赛',
    group_no    INT          DEFAULT 1 COMMENT '分组号',
    event_date  DATE         NOT NULL COMMENT '比赛日期',
    start_time  TIME         NOT NULL COMMENT '开始时间',
    end_time    TIME         DEFAULT NULL COMMENT '结束时间',
    venue_id    BIGINT       DEFAULT NULL COMMENT '场地ID',
    venue_name  VARCHAR(100) DEFAULT NULL COMMENT '场地名称',
    referee_id  BIGINT       DEFAULT NULL COMMENT '裁判员ID',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未开始 1-进行中 2-已完成 3-已取消',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛程安排表';

-- 赛程-运动员关联表
DROP TABLE IF EXISTS t_schedule_athlete;
CREATE TABLE t_schedule_athlete (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    schedule_id BIGINT   NOT NULL COMMENT '赛程ID',
    user_id     BIGINT   NOT NULL COMMENT '运动员用户ID',
    lane_no     INT      DEFAULT NULL COMMENT '道次/序号',
    status      TINYINT  DEFAULT 0 COMMENT '状态：0-正常 1-弃权 2-犯规',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赛程运动员关联表';

-- 成绩记录表
DROP TABLE IF EXISTS t_score;
CREATE TABLE t_score (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
    user_id      BIGINT        NOT NULL COMMENT '运动员用户ID',
    event_id     BIGINT        NOT NULL COMMENT '比赛项目ID',
    meeting_id   BIGINT        NOT NULL COMMENT '运动会ID',
    schedule_id  BIGINT        DEFAULT NULL COMMENT '赛程ID',
    score_value  DECIMAL(10,3) DEFAULT NULL COMMENT '成绩数值',
    score_text   VARCHAR(50)   DEFAULT NULL COMMENT '成绩文本表示',
    ranking      INT           DEFAULT NULL COMMENT '排名',
    points       INT           DEFAULT 0 COMMENT '积分',
    is_record    TINYINT       DEFAULT 0 COMMENT '是否破纪录',
    status       TINYINT       NOT NULL DEFAULT 0 COMMENT '状态：0-待录入 1-已录入 2-已确认 3-已公布',
    remark       VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    recorded_by  BIGINT        DEFAULT NULL COMMENT '录入裁判ID',
    confirmed_by BIGINT        DEFAULT NULL COMMENT '确认人ID',
    deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩记录表';

-- 团体积分表
DROP TABLE IF EXISTS t_team_score;
CREATE TABLE t_team_score (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    meeting_id  BIGINT       NOT NULL COMMENT '运动会ID',
    college     VARCHAR(100) NOT NULL COMMENT '学院名称',
    total_score INT          DEFAULT 0 COMMENT '总积分',
    gold        INT          DEFAULT 0 COMMENT '金牌数',
    silver      INT          DEFAULT 0 COMMENT '银牌数',
    bronze      INT          DEFAULT 0 COMMENT '铜牌数',
    ranking     INT          DEFAULT NULL COMMENT '团体排名',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团体积分表';

-- 通知公告与消息 (暂时放在 sports-service)
DROP TABLE IF EXISTS t_notice;
CREATE TABLE t_notice (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    title       VARCHAR(200) NOT NULL COMMENT '通知标题',
    content     TEXT         NOT NULL COMMENT '通知内容',
    type        TINYINT      NOT NULL DEFAULT 0 COMMENT '类型',
    meeting_id  BIGINT       DEFAULT NULL COMMENT '关联运动会ID',
    target_role TINYINT      DEFAULT NULL COMMENT '目标角色',
    is_top      TINYINT      DEFAULT 0 COMMENT '是否置顶',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态',
    published_by BIGINT      DEFAULT NULL COMMENT '发布人ID',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    title       VARCHAR(200) NOT NULL COMMENT '消息标题',
    content     TEXT         NOT NULL COMMENT '消息内容',
    type        TINYINT      NOT NULL DEFAULT 0 COMMENT '类型',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读',
    related_id  BIGINT       DEFAULT NULL COMMENT '关联业务ID',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息表';

-- Default venues
INSERT INTO t_venue (name, location, capacity, type, status) VALUES
('田径场A区', '东操场', 500, '田径场', 1),
('田径场B区', '东操场', 300, '田径场', 1),
('篮球馆', '体育馆一楼', 200, '体育馆', 1),
('游泳馆', '游泳中心', 150, '游泳池', 1),
('跳远沙坑', '东操场西侧', 50, '田赛场地', 1);

-- 初始业务数据
INSERT INTO t_meeting (id, name, description, start_date, end_date, location, status, enroll_start, enroll_end) VALUES
(1, '2024年度春季田径运动会', '这是一年一度的全校春季田径运动会，旨在增强学生体质，展现青春活力。', '2024-04-20', '2024-04-22', '校本部主体育场', 3, '2024-03-01 08:00:00', '2024-04-10 18:00:00');

INSERT INTO t_event (id, meeting_id, name, category, gender_limit, max_participants, event_date, start_time, venue, score_type, score_unit, status, referee_id) VALUES
(1, 1, '男子100米', '径赛', 0, 48, '2024-04-20', '09:00:00', '主体育场-跑道', 0, '秒', 1, 2),
(2, 1, '女子100米', '径赛', 1, 48, '2024-04-20', '10:00:00', '主体育场-跑道', 0, '秒', 1, 3),
(3, 1, '男子跳远', '田赛', 0, 20, '2024-04-20', '09:30:00', '跳远沙坑', 1, '米', 1, 10),
(4, 1, '男子铅球', '田赛', 0, 20, '2024-04-21', '08:30:00', '主体育场-北侧草坪', 1, '米', 0, 11);

INSERT INTO t_notice (title, content, type, meeting_id, is_top, status) VALUES
('关于2024春季运动会报名的通知', '各位同学，2024年春季运动会报名已经开始，请通过系统及时完成报名。', 0, 1, 1, 1);
