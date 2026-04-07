package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_score")
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long eventId;

    private Long meetingId;

    private Long scheduleId;

    private BigDecimal scoreValue;

    private String scoreText;

    private Integer ranking;

    private Integer points;

    private Integer isRecord;

    private Integer status;

    private String remark;

    private Long recordedBy;

    private Long confirmedBy;

    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userRealName;

    @TableField(exist = false)
    private String userCollege;

    @TableField(exist = false)
    private String eventName;

    @TableField(exist = false)
    private String meetingName;

    @TableField(exist = false)
    private String scoreUnit;

    @TableField(exist = false)
    private Integer scoreType;

    @TableField(exist = false)
    private String recorderName;
}
