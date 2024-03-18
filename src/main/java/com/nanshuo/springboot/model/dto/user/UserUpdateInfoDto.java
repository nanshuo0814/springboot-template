package com.nanshuo.springboot.model.dto.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新信息dto
 *
 * @author nanshuo
 * @date 2024/01/23 16:51:11
 */
@Data
@ApiModel(value = "UserUpdateInfoDto", description = "用户更新信息dto")
public class UserUpdateInfoDto implements Serializable {

    private static final long serialVersionUID = 7658342535926195857L;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.USERNAME, regexErrorMsg = "用户名只能包含字母、数字或汉字")
    private String userName;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱")
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    @CheckParam(required = NumberConstant.FALSE_VALUE,regex = UserRegexEnums.USER_GENDER, regexErrorMsg = "性别参数错误,请输入对应的性别,0:女,1:男,2:未知")
    private Integer userGender;

    /**
     * 简介
     */
    @ApiModelProperty(value = "用户简介")
    @CheckParam(required = NumberConstant.FALSE_VALUE, maxLength = 200, lenghtErrorMsg = "用户简介长度必须在1-200字之间")
    private String userProfile;
}
