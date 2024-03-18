package com.nanshuo.springboot.model.dto.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新dto
 *
 * @author 小鱼儿
 * @date 2024/01/06 16:39:54
 */
@Data
@ApiModel(value = "UserUpdateDto", description = "用户更新信息DTO")
public class UserUpdateDto implements Serializable {

    private static final long serialVersionUID = -4905623571700412110L;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    @CheckParam(nullErrorMsg = "用户id不能为空", regex = UserRegexEnums.USER_ID, regexErrorMsg = "用户id格式不正确")
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.USERNAME, regexErrorMsg = "用户昵称只能包含字母、数字或汉字")
    private String userName;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.ACCOUNT, regexErrorMsg = "用户账号只能包含字母、数字或下划线")
    private String userAccount;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private Integer userGender;

    /**
     * 简介
     */
    @ApiModelProperty(value = "用户简介", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, maxLength = 200, lenghtErrorMsg = "用户简介长度必须在1-200字之间")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userRole;
}
