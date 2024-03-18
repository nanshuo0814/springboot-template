package com.nanshuo.springboot.model.dto.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码重置dto
 *
 * @author 小鱼儿
 * @date 2024/01/04 21:24:00
 */
@Data
@ApiModel(value = "UserPasswordResetDto", description = "用户密码重置DTO")
public class UserPasswordResetDto implements Serializable {

    private static final long serialVersionUID = 7417360309354655142L;

    @ApiModelProperty(value = "用户账号", required = true)
    @CheckParam(nullErrorMsg = "用户账号不能为空", regex = UserRegexEnums.ACCOUNT, regexErrorMsg = "请输入正确的用户账号")
    private String userAccount;

    @ApiModelProperty(value = "用户密码", required = true)
    @CheckParam(nullErrorMsg = "用户密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "用户密码长度必须在6-18之间")
    private String userPassword;

    @ApiModelProperty(value = "第二遍输入的密码", required = true)
    @CheckParam(nullErrorMsg = "第二遍输入的密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的密码长度必须在6-18之间")
    private String checkPassword;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(nullErrorMsg = "邮箱不能为空", regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String userEmail;

    @ApiModelProperty(value = "邮箱验证码", required = true)
    @CheckParam(nullErrorMsg = "邮箱验证码不能为空", regex = UserRegexEnums.EMAIL_CAPTCHA, regexErrorMsg = "邮箱验证码错误")
    private String emailCaptcha;

}
