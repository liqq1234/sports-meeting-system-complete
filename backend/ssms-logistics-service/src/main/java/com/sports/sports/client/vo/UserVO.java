package com.sports.sports.client.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String college;
    private String className;
    private Integer gender;
    private Integer role;
}
