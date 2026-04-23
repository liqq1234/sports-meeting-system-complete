-- ==========================================================
-- 高校体育运动会管理系统 - 全量数据库初始化脚本
-- 包含内容：数据库创建、表结构初始化、完整业务基础数据
-- ==========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------
-- 1. SSMS_AUTH (用户中心)
-- ----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ssms_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_auth;

DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名/学号/工号',
    password    VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    gender      TINYINT      DEFAULT 0 COMMENT '性别：0-男 1-女',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    college     VARCHAR(100) DEFAULT NULL COMMENT '学院',
    class_name  VARCHAR(100) DEFAULT NULL COMMENT '班级',
    role        TINYINT      NOT NULL DEFAULT 2 COMMENT '角色：0-管理员 1-裁判员 2-运动员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 基础用户数据 (密码均为 123456)
INSERT INTO t_user (id, username, password, real_name, gender, phone, college, class_name, role, status) VALUES
(1, 'admin', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '系统管理员', 0, '13800000001', '教务处', NULL, 0, 1),
(2, 'referee01', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '裁判员张老师', 0, '13800000002', '体育部', NULL, 1, 1),
(3, 'referee02', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '裁判员李老师', 1, '13800000003', '体育部', NULL, 1, 1),
(4, '2024001', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '王小明', 0, '13800000004', '软件学院', '软件2201', 2, 1),
(5, '2024002', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '李小红', 1, '13800000005', '计算机学院', '计科2201', 2, 1),
(6, '2024003', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '张小刚', 0, '13800000006', '电子学院', '电子2201', 2, 1),
(10, 'referee03', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '陈平', 1, 1, '体育学院', NULL, 1, 1),
(11, 'referee04', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '赵强', 1, 1, '体育学院', NULL, 1, 1),
(12, '2024005', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '刘晨', 0, '13800000007', '计算机学院', '计科2201', 2, 1),
(13, '2024006', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '陈明', 0, '13800000008', '经管学院', '工商2302', 2, 1),
(14, '2024007', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '杨洋', 0, '13800000009', '机械学院', '机电2203', 2, 1),
(15, '2024008', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '黄超', 0, '13800000010', '自动化学院', '自控2201', 2, 1);

-- ----------------------------------------------------------
-- 2. SSMS_SPORTS (赛事核心)
-- ----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ssms_sports DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_sports;

DROP TABLE IF EXISTS t_meeting;
CREATE TABLE t_meeting (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    start_date  DATE         NOT NULL,
    end_date    DATE         NOT NULL,
    location    VARCHAR(200),
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '0-筹备 1-报名 2-截止 3-进行 4-结束',
    enroll_start DATETIME,
    enroll_end   DATETIME,
    created_by  BIGINT,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_event;
CREATE TABLE t_event (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    meeting_id    BIGINT       NOT NULL,
    name          VARCHAR(100) NOT NULL,
    category      VARCHAR(50),
    gender_limit  TINYINT,
    max_participants INT       DEFAULT 0,
    min_participants INT       DEFAULT 0,
    event_date    DATE,
    start_time    TIME,
    end_time      TIME,
    venue         VARCHAR(100),
    score_type    TINYINT      NOT NULL DEFAULT 0,
    score_unit    VARCHAR(20),
    rules         TEXT,
    status        TINYINT      NOT NULL DEFAULT 0,
    referee_id    BIGINT,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_venue;
CREATE TABLE t_venue (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    location    VARCHAR(200),
    capacity    INT          DEFAULT 0,
    type        VARCHAR(50),
    status      TINYINT      NOT NULL DEFAULT 1,
    description TEXT,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_registration;
CREATE TABLE t_registration (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    user_id       BIGINT   NOT NULL,
    event_id      BIGINT   NOT NULL,
    meeting_id    BIGINT   NOT NULL,
    status        TINYINT  NOT NULL DEFAULT 0,
    reject_reason VARCHAR(500),
    reviewed_by   BIGINT,
    review_time   DATETIME,
    remark        VARCHAR(500),
    deleted       TINYINT  NOT NULL DEFAULT 0,
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_schedule;
CREATE TABLE t_schedule (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    meeting_id  BIGINT       NOT NULL,
    event_id    BIGINT       NOT NULL,
    round       VARCHAR(50)  DEFAULT '决赛',
    group_no    INT          DEFAULT 1,
    event_date  DATE         NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME,
    venue_id    BIGINT,
    venue_name  VARCHAR(100),
    referee_id  BIGINT,
    status      TINYINT      NOT NULL DEFAULT 0,
    remark      VARCHAR(500),
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_score;
CREATE TABLE t_score (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      BIGINT        NOT NULL,
    event_id     BIGINT        NOT NULL,
    meeting_id   BIGINT        NOT NULL,
    schedule_id  BIGINT,
    score_value  DECIMAL(10,3),
    score_text   VARCHAR(50),
    ranking      INT,
    points       INT           DEFAULT 0,
    is_record    TINYINT       DEFAULT 0,
    status       TINYINT       NOT NULL DEFAULT 0,
    remark       VARCHAR(500),
    recorded_by  BIGINT,
    confirmed_by BIGINT,
    deleted      TINYINT       NOT NULL DEFAULT 0,
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_notice;
CREATE TABLE t_notice (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    type        TINYINT      NOT NULL DEFAULT 0,
    meeting_id  BIGINT,
    target_role TINYINT,
    is_top      TINYINT      DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1,
    published_by BIGINT,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    type        TINYINT      NOT NULL DEFAULT 0,
    is_read     TINYINT      NOT NULL DEFAULT 0,
    related_id  BIGINT,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始业务数据
INSERT INTO t_meeting (id, name, description, start_date, end_date, location, status, enroll_start, enroll_end) VALUES
(1, '2026年度春季田径运动会', '全校年度体育盛事', '2026-04-20', '2026-04-22', '校本部主体育场', 1, '2026-03-01 08:00:00', '2026-12-31 23:59:59');

INSERT INTO t_event (id, meeting_id, name, category, gender_limit, max_participants, event_date, start_time, venue, score_type, score_unit, status, referee_id) VALUES
(1, 1, '男子100米', '径赛', 0, 48, '2026-04-20', '09:00:00', '主体育场-跑道', 0, '秒', 1, 2),
(2, 1, '女子100米', '径赛', 1, 48, '2026-04-20', '10:00:00', '主体育场-跑道', 0, '秒', 1, 3),
(3, 1, '男子跳远', '田赛', 0, 20, '2026-04-20', '09:30:00', '跳远沙坑', 1, '米', 1, 10),
(4, 1, '男子铅球', '田赛', 0, 20, '2026-04-21', '08:30:00', '主体育场-北侧草坪', 1, '米', 0, 11),
(5, 1, '女子400米', '径赛', 1, 32, '2026-04-21', '14:00:00', '主体育场-跑道', 0, '秒', 0, 2);

INSERT INTO t_notice (title, content, type, meeting_id, is_top, status) VALUES
('关于2026年春季运动会报名的通知', '报名已开启，请尽快完成报名。', 0, 1, 1, 1);

-- ----------------------------------------------------------
-- 3. SSMS_LOGISTICS (后勤物资)
-- ----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ssms_logistics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_logistics;

DROP TABLE IF EXISTS t_material;
CREATE TABLE t_material (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    spec        VARCHAR(50),
    unit        VARCHAR(20),
    stock       INT          NOT NULL DEFAULT 0,
    threshold   INT          NOT NULL DEFAULT 10,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_material (name, spec, unit, stock, threshold) VALUES
('电子秒表', '精确度0.01s', '块', 50, 5),
('发令枪', '专业型', '支', 5, 1),
('接力棒', '铝合金', '根', 20, 4),
('号码牌', '反光', '套', 1000, 100);

-- ----------------------------------------------------------
-- 4. SSMS_MEDICAL (医疗保障)
-- ----------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ssms_medical DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_medical;

DROP TABLE IF EXISTS t_medical_record;
CREATE TABLE t_medical_record (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    meeting_id  BIGINT   NOT NULL,
    patient_id  BIGINT   NOT NULL,
    visit_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    symptoms    TEXT,
    diagnosis   TEXT,
    treatment   TEXT,
    doctor      VARCHAR(50),
    disposition TINYINT  DEFAULT 0,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_medical_record (patient_id, symptoms, diagnosis, treatment, meeting_id, visit_time, doctor) VALUES
(4, '肌肉抽搐', '腓肠肌痉挛', '按摩', 1, '2026-04-20 09:15:00', '张医生');

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 初始化完成
-- 默认管理员账号：admin
-- 默认密码：123456
-- ==========================================================