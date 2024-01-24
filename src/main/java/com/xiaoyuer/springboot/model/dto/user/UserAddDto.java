package com.xiaoyuer.springboot.model.dto.user;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加dto
 *
 * @author 小鱼儿
 * @date 2024/01/06 12:00:19
 */
@Data
@ApiModel(value = "UserAddDto", description = "用户添加信息DTO")
public class UserAddDto implements Serializable {

    private static final long serialVersionUID = -119754408044041182L;

    /**
     * 用户昵称(不是必须的，可以设置默认昵称或者留空，如：“小鱼儿”)
     */
    @ApiModelProperty(value = "用户昵称", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.USERNAME, regexErrorMsg = "用户名只能包含字母、数字或汉字")
    private String userName;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(nullErrorMsg = "账号不能为空", minLength = 3, maxLength = 11, lenghtErrorMsg = "账号长度必须在3-11之间", regex = UserRegexEnums.ACCOUNT, regexErrorMsg = "账号必须以字母开头且只能包含字母、数字或下划线")
    private String userAccount;

    /**
     * 用户密码(不是必须的，如果不写，则使用默认密码)
     */
    @ApiModelProperty(value = "密码", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, minLength = 6, maxLength = 18, lenghtErrorMsg = "密码长度必须在6-18之间", regex = UserRegexEnums.PASSWORD, regexErrorMsg = "密码必须包含字母、数字,可以有特殊字符")
    private String userPassword;

    /**
     * 用户邮箱(不是必须的)
     */
    @ApiModelProperty(value = "邮箱", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确")
    private String userEmail;

    /**
     * 用户简介(不是必须的，可以设置默认昵称或者留空，如：“一条只会冒泡的小鱼儿ya”)
     */
    @ApiModelProperty(value = "用户简介", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, maxLength = 200, lenghtErrorMsg = "用户简介长度必须在1-200字之间")
    private String userProfile;

    /**
     * 用户性别(不是必须的，默认是未知，0:女, 1:男, 2:未知)
     */
    @ApiModelProperty(value = "用户性别", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE,regex = UserRegexEnums.USER_GENDER, regexErrorMsg = "性别参数错误,请输入对应的性别,0:女,1:男,2:未知")
    private Integer userGender;

    /**
     * 用户头像(不是必须的,若没有上传头像可以使用默认头像或无头像)
     */
    @ApiModelProperty(value = "用户头像", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE)
    private String userAvatar;

    /**
     * 用户角色(不是必须的): user(默认), admin
     */
    @ApiModelProperty(value = "用户角色", required = false)
    @CheckParam(required = NumberConstant.FALSE_VALUE, regex = UserRegexEnums.USER_ROLE, regexErrorMsg = "用户角色只能是user或admin")
    private String userRole;
}
