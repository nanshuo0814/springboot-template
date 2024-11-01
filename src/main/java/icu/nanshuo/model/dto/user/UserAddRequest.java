package icu.nanshuo.model.dto.user;

import icu.nanshuo.annotation.CheckParam;
import icu.nanshuo.constant.NumberConstant;
import icu.nanshuo.constant.VerifyParamRegexConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加Request
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/06 12:00:19
 */
@Data
@ApiModel(value = "UserAddRequest", description = "用户添加信息请求DTO")
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = -119754408044041182L;

    /**
     * 用户昵称(不是必须的，可以设置默认昵称或者留空，如：“nanshuo/南烁”)
     */
    @ApiModelProperty(value = "用户昵称")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USERNAME, alias = "用户名")
    private String userName;

    /**
     * 账号（不是必须的，默认值可以是用 user + 时间戳）
     */
    @ApiModelProperty(value = "账号")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "账号", minLength = 3, maxLength = 11, regex = VerifyParamRegexConstant.ACCOUNT)
    private String userAccount;

    /**
     * 用户密码(不是必须的，如果不写，则使用默认密码)
     */
    @ApiModelProperty(value = "密码")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, minLength = 6, maxLength = 18, regex = VerifyParamRegexConstant.PASSWORD, alias = "密码")
    private String userPassword;

    /**
     * 用户邮箱(不是必须的)
     */
    @ApiModelProperty(value = "邮箱")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.EMAIL, alias = "邮箱")
    private String userEmail;

    /**
     * 用户简介(不是必须的，可以设置默认昵称或者留空)
     */
    @ApiModelProperty(value = "用户简介")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, maxLength = 200, alias = "用户简介")
    private String userProfile;

    /**
     * 用户性别(不是必须的，默认是未知，0:女, 1:男, 2:未知)
     */
    @ApiModelProperty(value = "用户性别")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USER_GENDER, alias = "性别")
    private Integer userGender;

    /**
     * 用户头像(不是必须的,若没有上传头像可以使用默认头像或无头像)
     */
    @ApiModelProperty(value = "用户头像")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE)
    private String userAvatar;

    /**
     * 用户角色(不是必须的): user(默认), admin
     */
    @ApiModelProperty(value = "用户角色")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USER_ROLE, alias = "用户角色")
    private String userRole;
}
