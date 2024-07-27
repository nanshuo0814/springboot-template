package com.nanshuo.icu.model.dto.user;

import com.nanshuo.icu.annotation.CheckParam;
import com.nanshuo.icu.constant.VerifyParamRegexConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码重置Request
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/04 21:24:00
 */
@Data
@ApiModel(value = "UserPasswordResetRequest", description = "用户密码重置请求DTO")
public class UserPasswordResetRequest implements Serializable {

    private static final long serialVersionUID = 7417360309354655142L;

    @ApiModelProperty(value = "用户账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 16, regex = VerifyParamRegexConstant.ACCOUNT)
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(alias = "用户密码", minLength = 6, maxLength = 18,regex = VerifyParamRegexConstant.PASSWORD)
    private String userPassword;

    @ApiModelProperty(value = "确认密码", required = true)
    @CheckParam(alias = "确认密码", minLength = 6, maxLength = 18, regex = VerifyParamRegexConstant.PASSWORD)
    private String checkPassword;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(alias = "邮箱", regex = VerifyParamRegexConstant.EMAIL)
    private String userEmail;

    @ApiModelProperty(value = "邮箱验证码", required = true)
    @CheckParam(alias = "邮箱验证码",regex = VerifyParamRegexConstant.EMAIL_CAPTCHA)
    private String emailCaptcha;

}
