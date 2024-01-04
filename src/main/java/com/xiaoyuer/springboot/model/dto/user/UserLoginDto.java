package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import lombok.Data;

/**
 * 用户登录dto
 *
 * @author 小鱼儿
 * @date 2024/01/04 18:58:48
 */
@Data
public class UserLoginDto {

    /**
     * 账号
     */
    @CheckParam(nullErrorMsg = "账号不能为空", minLength = 3, maxLength = 16, lenghtErrorMsg = "账号长度必须在3-16之间")
    private String userAccount;

    /**
     * 用户密码
     */
    @CheckParam(nullErrorMsg = "密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "密码长度必须在6-18之间")
    private String userPassword;

    /**
     * 图片验证码
     */
    @CheckParam(nullErrorMsg = "图片验证码不能为空", regex = VerifyRegexEnums.IMAGE_CODE, regexErrorMsg = "图片验证码必须是6位")
    private String imageCode;
}
