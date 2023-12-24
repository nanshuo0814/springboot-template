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

    @CheckParam(regex = VerifyRegexEnums.USERNAME)
    private String userName;

    @CheckParam(regex = VerifyRegexEnums.ACCOUNT)
    private String userAccount;

    @CheckParam(regex = VerifyRegexEnums.PASSWORD)
    private String userPassword;

    @CheckParam(regex = VerifyRegexEnums.PASSWORD)
    private String checkPassword;

    @CheckParam(regex = VerifyRegexEnums.EMAIL)
    private String email;

    @CheckParam(regex = VerifyRegexEnums.CHECK_CODE)
    private String checkCode;
}
