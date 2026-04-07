-- ssms_auth database initialization
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

-- Default users
INSERT INTO t_user (username, password, real_name, gender, phone, college, class_name, role, status) VALUES
('admin', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '系统管理员', 0, '13800000001', '教务处', NULL, 0, 1),
('referee01', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '裁判员张老师', 0, '13800000002', '体育部', NULL, 1, 1),
('referee02', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '裁判员李老师', 1, '13800000003', '体育部', NULL, 1, 1),
('2024001', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '王小明', 0, '13800000004', '软件学院', '软件2201', 2, 1),
('2024002', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '李小红', 1, '13800000005', '计算机学院', '计科2201', 2, 1),
('2024003', '$2a$10$vfAH3yfHCjH7ynR9wnyt3.OHcxdzIfJltXS4IBDiAytg0QphRZNVu', '张小刚', 0, '13800000006', '电子学院', '电子2201', 2, 1);
