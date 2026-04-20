package com.sports.sports.client.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "Feign 远程调用用户展示信息实体")
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("所属学院")
    private String college;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("角色类型（0管理员，1裁判员，2运动员）")
    private Integer role;
}
