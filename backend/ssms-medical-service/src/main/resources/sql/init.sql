-- ssms_medical database initialization
CREATE DATABASE IF NOT EXISTS ssms_medical DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ssms_medical;

-- 医疗就诊记录
DROP TABLE IF EXISTS t_medical_record;
CREATE TABLE t_medical_record (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '就诊ID',
    meeting_id  BIGINT   NOT NULL COMMENT '运动会ID',
    patient_id  BIGINT   NOT NULL COMMENT '就诊运动员ID',
    visit_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '就诊时间',
    symptoms    TEXT     DEFAULT NULL COMMENT '主诉/症状',
    diagnosis   TEXT     DEFAULT NULL COMMENT '诊断结果',
    treatment   TEXT     DEFAULT NULL COMMENT '处置说明',
    doctor      VARCHAR(50)  DEFAULT NULL COMMENT '接诊医生',
    disposition TINYINT  DEFAULT 0 COMMENT '处置去向：0-返回比赛 1-留院观察 2-转院',
    deleted     TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医疗就诊记录表';

-- 初始业务数据
INSERT INTO t_medical_record (patient_id, symptoms, diagnosis, treatment, meeting_id, visit_time, doctor, disposition) VALUES
(4, '百米冲刺后小腿肌肉抽搐', '小腿腓肠肌痉挛', '按摩推拿，喷涂云南白药', 1, '2024-04-20 09:15:00', '张医生', 0),
(12, '跳远试跳落坑时扭伤脚踝', '左踝关节轻度扭伤', '冰敷压迫，建议观察', 1, '2024-04-20 10:20:00', '王护士', 1),
(5, '终点冲刺后晕倒', '运动性低血糖、轻微中暑', '葡萄糖补充，阴凉处休息', 1, '2024-04-20 10:45:00', '李医生', 1);
