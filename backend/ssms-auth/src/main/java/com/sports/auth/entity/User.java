package com.sports.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String realName;

    private Integer gender;

    private String phone;

    private String email;

    private String avatar;

    private String college;

    private String className;

    private Integer role;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String token;

    @TableField(exist = false)
    private String roleName;

    public String getRoleName() {
        if (role == null) return "";
        switch (role) {
            case 0: return "管理员";
            case 1: return "裁判员";
            case 2: return "运动员";
            default: return "未知";
        }
    }
}
