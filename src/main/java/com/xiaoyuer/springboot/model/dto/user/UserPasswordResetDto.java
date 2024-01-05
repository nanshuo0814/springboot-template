package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码重置dto
 *
 * @author 小鱼儿
 * @date 2024/01/04 21:24:00
 */
@Data
public class UserPasswordResetDto implements Serializable {

    private static final long serialVersionUID = 7417360309354655142L;

    @CheckParam(nullErrorMsg = "用户账号不能为空", regex = VerifyRegexEnums.ACCOUNT, regexErrorMsg = "请输入正确的用户账号")
    private String userAccount;

    @CheckParam(nullErrorMsg = "用户密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "用户密码长度必须在6-18之间")
    private String userPassword;

    @CheckParam(nullErrorMsg = "第二遍输入的密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的密码长度必须在6-18之间")
    private String checkPassword;

    @CheckParam(nullErrorMsg = "邮箱不能为空", regex = VerifyRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String email;

    @CheckParam(nullErrorMsg = "邮箱验证码不能为空", regex = VerifyRegexEnums.EMAIL_CAPTCHA, regexErrorMsg = "邮箱验证码错误")
    private String emailCaptcha;

}
