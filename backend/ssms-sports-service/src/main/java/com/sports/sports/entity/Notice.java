package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_notice")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private Integer type;

    private Long meetingId;

    private Integer targetRole;

    private Integer isTop;

    private Integer status;

    private Long publishedBy;

    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String publisherName;

    @TableField(exist = false)
    private String meetingName;

    @TableField(exist = false)
    private String typeName;

    public String getTypeName() {
        if (type == null) return "";
        switch (type) {
            case 0: return "系统公告";
            case 1: return "赛程变更";
            case 2: return "审核通知";
            case 3: return "成绩公布";
            default: return "未知";
        }
    }
}
