package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_registration")
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long eventId;

    private Long meetingId;

    private Integer status;

    private String rejectReason;

    private Long reviewedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    private String remark;

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
    private String userClassName;

    @TableField(exist = false)
    private String eventName;

    @TableField(exist = false)
    private String meetingName;

    @TableField(exist = false)
    private String reviewerName;

    @TableField(exist = false)
    private String statusName;

    public String getStatusName() {
        if (status == null) return "";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核驳回";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
