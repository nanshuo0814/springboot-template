package com.xiaoyuer.springboot.model.vo.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录vo
 *
 * @author 小鱼儿
 * @date 2024/01/04 19:08:31
 */
@Data
@ApiModel(value = "UserLoginVO", description = "用户登录VO")
public class UserLoginVO implements Serializable {

    /**
     * 用户 id
     */
    @ApiModelProperty(value = "用户 id", required = true)
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", required = true)
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", required = true)
    private String userAvatar;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = true)
    private String userAccount;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别", required = true)
    private Integer userGender;

    /**
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介", required = true)
    private String userProfile;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱", required = true)
    private String userEmail;


    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色：user/admin/ban", required = true)
    private String userRole;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = true)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", required = true)
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}