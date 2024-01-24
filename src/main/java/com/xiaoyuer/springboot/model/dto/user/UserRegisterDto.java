package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册 DTO
 *
 * @author 小鱼儿
 * @date 2023/12/23 19:00:34
 */
@Data
@ApiModel(value = "UserRegisterDto", description = "用户注册信息DTO")
public class UserRegisterDto implements Serializable {

    private static final long serialVersionUID = -3801105286374526414L;

    @ApiModelProperty(value = "用户名", required = true)
    @CheckParam(nullErrorMsg = "用户名不能为空", regex = UserRegexEnums.USERNAME, regexErrorMsg = "用户名只能包含字母、数字或汉字")
    private String userName;

    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(nullErrorMsg = "账号不能为空", minLength = 3, maxLength = 11, lenghtErrorMsg = "账号长度必须在3-11之间", regex = UserRegexEnums.ACCOUNT, regexErrorMsg = "账号必须以字母开头且只能包含字母、数字或下划线")
    private String userAccount;

    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(nullErrorMsg = "密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "密码长度必须在6-18之间", regex = UserRegexEnums.PASSWORD, regexErrorMsg = "密码必须包含字母、数字或特殊字符")
    private String userPassword;

    @ApiModelProperty(value = "第二遍输入的密码", required = true)
    @CheckParam(nullErrorMsg = "第二遍输入的密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的密码长度必须在6-18之间", regex = UserRegexEnums.PASSWORD, regexErrorMsg = "第二遍输入的密码必须包含字母、数字或特殊字符")
    private String checkPassword;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(nullErrorMsg = "邮箱不能为空", regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "邮箱验证码", required = true)
    @CheckParam(nullErrorMsg = "邮箱验证码不能为空", regex = UserRegexEnums.EMAIL_CAPTCHA, regexErrorMsg = "邮箱验证码错误")
    private String emailCaptcha;

    @ApiModelProperty(value = "图片验证码", required = true)
    @CheckParam(nullErrorMsg = "图片验证码不能为空", regex = UserRegexEnums.IMAGE_CAPTCHA, regexErrorMsg = "图片验证码错误")
    private String imageCaptcha;
}
