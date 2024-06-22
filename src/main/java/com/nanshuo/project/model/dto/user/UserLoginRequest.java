package com.nanshuo.project.model.dto.user;

import com.nanshuo.project.annotation.CheckParam;
import com.nanshuo.project.constant.NumberConstant;
import com.nanshuo.project.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录Request
 *
 * @author nanshuo
 * @date 2024/01/04 18:58:48
 */
@Data
@ApiModel(value = "UserLoginRequest", description = "用户登录信息Request")
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = -5262836669010105900L;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 16, regex = UserRegexEnums.ACCOUNT)
    private String userAccount;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(alias = "密码", minLength = 6, maxLength = 18,regex = UserRegexEnums.PASSWORD)
    private String userPassword;

    /**
     * 图片验证码
     */
    @ApiModelProperty(value = "图片验证码")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "图片验证码", regex = UserRegexEnums.IMAGE_CAPTCHA)
    private String imageCaptcha;

    /**
     * 图片验证码key
     */
    @ApiModelProperty(value = "图片验证码key")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "图片验证码key")
    private String captchaKey;

}
