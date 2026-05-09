package com.sports.sports.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("t_schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long meetingId;

    private Long eventId;

    @JsonProperty("round")
    @TableField("round")
    private String round;

    private Integer groupNo;

    @JsonProperty("eventDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("event_date")
    private LocalDate eventDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Long venueId;

    private String venueName;

    private Long refereeId;

    private Integer status;

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
    private String eventName;

    @TableField(exist = false)
    private String meetingName;

    @TableField(exist = false)
    private String refereeName;

    @TableField(exist = false)
    private String category;

    @TableField(exist = false)
    private Integer athleteCount;
}
