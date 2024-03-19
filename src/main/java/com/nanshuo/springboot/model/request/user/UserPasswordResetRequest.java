package com.nanshuo.springboot.model.request.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码重置Request
 *
 * @author nanshuo
 * @date 2024/01/04 21:24:00
 */
@Data
@ApiModel(value = "UserPasswordResetRequest", description = "用户密码重置Request")
public class UserPasswordResetRequest implements Serializable {

    private static final long serialVersionUID = 7417360309354655142L;

    @ApiModelProperty(value = "用户账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 16, regex = UserRegexEnums.ACCOUNT)
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(alias = "用户密码", minLength = 6, maxLength = 18,regex = UserRegexEnums.PASSWORD)
    private String userPassword;

    @ApiModelProperty(value = "第二遍输入的密码", required = true)
    @CheckParam(alias = "第二遍输入的密码", minLength = 6, maxLength = 18, regex = UserRegexEnums.PASSWORD)
    private String checkPassword;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(alias = "邮箱", regex = UserRegexEnums.EMAIL)
    private String userEmail;

    @ApiModelProperty(value = "邮箱验证码", required = true)
    @CheckParam(alias = "邮箱验证码",regex = UserRegexEnums.EMAIL_CAPTCHA)
    private String emailCaptcha;

}
