package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_meeting")
public class Meeting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String location;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollEnd;

    private Long createdBy;

    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String statusName;

    @TableField(exist = false)
    private Integer eventCount;

    @TableField(exist = false)
    private Integer registrationCount;

    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "筹备中";
            case 1: return "报名中";
            case 2: return "报名截止";
            case 3: return "进行中";
            case 4: return "已结束";
            default: return "未知";
        }
    }
}
