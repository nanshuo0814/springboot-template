package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码更新dto
 *
 * @author 小鱼儿
 * @date 2024/01/04 22:50:31
 */
@Data
public class UserPasswordUpdateDto implements Serializable {

    private static final long serialVersionUID = 8383202174723157092L;

    @CheckParam(nullErrorMsg = "原密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "原密码长度必须在6-18之间")
    private String oldPassword;

    @CheckParam(nullErrorMsg = "新密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "新密码长度必须在6-18之间")
    private String userPassword;

    @CheckParam(nullErrorMsg = "第二遍输入的新密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的新密码长度必须在6-18之间")
    private String checkPassword;

    @CheckParam(nullErrorMsg = "邮箱不能为空", regex = VerifyRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String email;

    @CheckParam(nullErrorMsg = "邮箱验证码不能为空", regex = VerifyRegexEnums.EMAIL_CAPTCHA, regexErrorMsg = "邮箱验证码错误")
    private String emailCaptcha;

}
