package com.nanshuo.project.model.dto.user;

import com.nanshuo.project.annotation.CheckParam;
import com.nanshuo.project.constant.NumberConstant;
import com.nanshuo.project.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加Request
 *
 * @author nanshuo
 * @date 2024/01/06 12:00:19
 */
@Data
@ApiModel(value = "UserAddRequest", description = "用户添加信息Request")
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = -119754408044041182L;

    /**
     * 用户昵称(不是必须的，可以设置默认昵称或者留空，如：“nanshuo/南烁”)
     */
    @ApiModelProperty(value = "用户昵称", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.USERNAME,alias = "用户名")
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 11, regex = UserRegexEnums.ACCOUNT)
    private String userAccount;

    /**
     * 用户密码(不是必须的，如果不写，则使用默认密码)
     */
    @ApiModelProperty(value = "密码", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, minLength = 6, maxLength = 18, regex = UserRegexEnums.PASSWORD,alias = "密码")
    private String userPassword;

    /**
     * 用户邮箱(不是必须的)
     */
    @ApiModelProperty(value = "邮箱", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.EMAIL,alias = "邮箱")
    private String userEmail;

    /**
     * 用户简介(不是必须的，可以设置默认昵称或者留空)
     */
    @ApiModelProperty(value = "用户简介", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, maxLength = 200, alias = "用户简介")
    private String userProfile;

    /**
     * 用户性别(不是必须的，默认是未知，0:女, 1:男, 2:未知)
     */
    @ApiModelProperty(value = "用户性别", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE,regex = UserRegexEnums.USER_GENDER,alias = "性别")
    private Integer userGender;

    /**
     * 用户头像(不是必须的,若没有上传头像可以使用默认头像或无头像)
     */
    @ApiModelProperty(value = "用户头像", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE)
    private String userAvatar;

    /**
     * 用户角色(不是必须的): user(默认), admin
     */
    @ApiModelProperty(value = "用户角色", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.USER_ROLE,alias = "用户角色")
    private String userRole;
}
