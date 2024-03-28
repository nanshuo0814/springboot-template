package com.nanshuo.springboot.model.dto.user;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册 Request
 *
 * @author nanshuo
 * @date 2023/12/23 19:00:34
 */
@Data
@ApiModel(value = "UserRegisterRequest", description = "用户注册信息Request")
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3801105286374526414L;

    @ApiModelProperty(value = "用户名", required = true)
    @CheckParam(alias = "用户名", regex = UserRegexEnums.USERNAME)
    private String userName;

    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 11, regex = UserRegexEnums.ACCOUNT)
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(alias = "密码", minLength = 6, maxLength = 18, regex = UserRegexEnums.PASSWORD)
    private String userPassword;

    @ApiModelProperty(value = "第二遍输入的密码", required = true)
    @CheckParam(alias = "第二遍密码", minLength = 6, maxLength = 18, regex = UserRegexEnums.PASSWORD)
    private String checkPassword;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(alias = "邮箱", regex = UserRegexEnums.EMAIL)
    private String email;

    @ApiModelProperty(value = "邮箱验证码", required = true)
    @CheckParam(alias = "邮箱验证码", regex = UserRegexEnums.EMAIL_CAPTCHA)
    private String emailCaptcha;

    @ApiModelProperty(value = "图片验证码", required = true)
    @CheckParam(alias = "图片验证码", regex = UserRegexEnums.IMAGE_CAPTCHA)
    private String imageCaptcha;

    /**
     * 图片验证码key
     */
    @ApiModelProperty(value = "图片验证码key", required = true)
    @CheckParam(alias = "图片验证码key")
    private String captchaKey;

}
