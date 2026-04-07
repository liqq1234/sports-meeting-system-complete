package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("t_event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long meetingId;

    private String name;

    private String category;

    private Integer genderLimit;

    private Integer maxParticipants;

    private Integer minParticipants;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String venue;

    private Integer scoreType;

    private String scoreUnit;

    private String rules;

    private Integer status;

    private Long refereeId;

    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String meetingName;

    @TableField(exist = false)
    private String refereeName;

    @TableField(exist = false)
    private Integer currentParticipants;

    @TableField(exist = false)
    private Boolean enrolled;
}
