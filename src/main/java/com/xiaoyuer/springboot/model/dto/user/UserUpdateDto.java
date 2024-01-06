package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新dto
 *
 * @author 小鱼儿
 * @date 2024/01/06 16:39:54
 */
@Data
public class UserUpdateDto implements Serializable {

    private static final long serialVersionUID = -4905623571700412110L;

    /**
     * 用户id
     */
    @CheckParam(nullErrorMsg = "用户id不能为空")
    private Long userId;

    /**
     * 用户昵称
     */
    @CheckParam(nullErrorMsg = "用户昵称不能为空", regex = VerifyRegexEnums.USERNAME, regexErrorMsg = "用户昵称只能包含字母、数字或汉字")
    private String userName;

    /**
     * 用户头像
     */
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = VerifyRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户性别
     */
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private Integer userGender;

    /**
     * 简介
     */
    @CheckParam(required = NumberConstant.FALSE_VALUE, maxLength = 200, lenghtErrorMsg = "用户简介长度必须在1-200字之间")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userRole;
}
