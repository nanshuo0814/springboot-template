package com.nanshuo.springboot.model.request.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新信息Request
 *
 * @author nanshuo
 * @date 2024/01/23 16:51:11
 */
@Data
@ApiModel(value = "UserUpdateInfoRequest", description = "用户更新信息Request")
public class UserUpdateInfoRequest implements Serializable {

    private static final long serialVersionUID = 7658342535926195857L;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.USERNAME,alias = "用户昵称")
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @CheckParam(required = NumberConstant.FALSE_VALUE,alias = "用户头像")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱")
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.EMAIL,alias = "用户邮箱")
    private String userEmail;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    @CheckParam(required = NumberConstant.FALSE_VALUE,regex = UserRegexEnums.USER_GENDER,alias = "性别")
    private Integer userGender;

    /**
     * 简介
     */
    @ApiModelProperty(value = "用户简介")
    @CheckParam(required = NumberConstant.FALSE_VALUE, maxLength = 200, alias = "用户简介")
    private String userProfile;
}
