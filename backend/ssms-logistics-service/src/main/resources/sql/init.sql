-- ssms_logistics database initialization
CREATE DATABASE IF NOT EXISTS ssms_logistics DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_logistics;

-- 物资基础表
DROP TABLE IF EXISTS t_material;
CREATE TABLE t_material (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '物资ID',
    name        VARCHAR(100) NOT NULL COMMENT '物资名称',
    spec        VARCHAR(50)  DEFAULT NULL COMMENT '规格',
    unit        VARCHAR(20)  DEFAULT NULL COMMENT '单位',
    stock       INT          NOT NULL DEFAULT 0 COMMENT '当前库存',
    threshold   INT          NOT NULL DEFAULT 10 COMMENT '预警阈值',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-否 1-是',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资库表';

-- 物资发放申请单
DROP TABLE IF EXISTS t_material_allocation;
CREATE TABLE t_material_allocation (
    id           BIGINT   NOT NULL AUTO_INCREMENT COMMENT '申请单ID',
    meeting_id   BIGINT   NOT NULL COMMENT '运动会ID',
    applicant_id BIGINT   NOT NULL COMMENT '申请人ID',
    purpose      VARCHAR(200) DEFAULT NULL COMMENT '用途说明',
    status       TINYINT  NOT NULL DEFAULT 0 COMMENT '状态：0-待审 1-批准 2-驳回 3-已分发',
    items        JSON     NOT NULL COMMENT '申请明细内容（JSON格式）',
    deleted      TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    update_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资发放单';

-- Default materials
INSERT INTO t_material (name, spec, unit, stock, threshold) VALUES
('电子秒表', '精确度0.01s', '块', 50, 5),
('发令枪', '专业比赛型', '支', 5, 1),
('接力棒', '标准铝合金', '根', 20, 4),
('号码牌', '反光纤维', '套', 1000, 100),
('医用药箱', '标准型', '箱', 10, 2);
