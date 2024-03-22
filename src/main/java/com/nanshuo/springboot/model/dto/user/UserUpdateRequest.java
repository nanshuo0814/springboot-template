package com.nanshuo.springboot.model.dto.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新Request
 *
 * @author nanshuo
 * @date 2024/01/06 16:39:54
 */
@Data
@ApiModel(value = "UserUpdateRequest", description = "用户更新信息Request")
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = -4905623571700412110L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    @CheckParam(alias = "用户id", regex = UserRegexEnums.USER_ID)
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.USERNAME,alias = "用户昵称")
    private String userName;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.ACCOUNT, alias = "用户账号")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE,alias = "用户头像")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.EMAIL, alias = "用户邮箱")
    private String userEmail;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE,regex = UserRegexEnums.USER_GENDER, alias = "用户性别")
    private Integer userGender;

    /**
     * 简介
     */
    @ApiModelProperty(value = "用户简介", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, maxLength = 200, alias = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = UserRegexEnums.USER_ROLE, alias = "用户角色")
    private String userRole;

}
