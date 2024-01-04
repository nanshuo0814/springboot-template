package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册 DTO
 *
 * @author 小鱼儿
 * @date 2023/12/23 19:00:34
 */
@Data
public class UserRegisterDto implements Serializable {

    @CheckParam(nullErrorMsg = "用户名不能为空", regex = VerifyRegexEnums.USERNAME, regexErrorMsg = "用户名只能包含字母、数字或汉字")
    private String userName;

    @CheckParam(nullErrorMsg = "账号不能为空", minLength = 3, maxLength = 11, lenghtErrorMsg = "账号长度必须在3-11之间", regex = VerifyRegexEnums.ACCOUNT, regexErrorMsg = "账号必须以字母开头且只能包含字母、数字或下划线")
    private String userAccount;

    @CheckParam(nullErrorMsg = "密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "密码长度必须在6-18之间", regex = VerifyRegexEnums.PASSWORD, regexErrorMsg = "密码必须包含字母、数字或特殊字符")
    private String userPassword;

    @CheckParam(nullErrorMsg = "第二遍输入的密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的密码长度必须在6-18之间", regex = VerifyRegexEnums.PASSWORD, regexErrorMsg = "第二遍输入的密码必须包含字母、数字或特殊字符")
    private String checkPassword;

    @CheckParam(nullErrorMsg = "邮箱不能为空", regex = VerifyRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String email;

    @CheckParam(nullErrorMsg = "邮箱验证码不能为空", regex = VerifyRegexEnums.EMAIL_CODE, regexErrorMsg = "邮箱验证码必须是6位")
    private String emailCode;

    @CheckParam(nullErrorMsg = "图片验证码不能为空", regex = VerifyRegexEnums.IMAGE_CODE, regexErrorMsg = "图片验证码必须是6位")
    private String imageCode;
}
