package icu.nanshuo.model.dto.user;

import icu.nanshuo.annotation.CheckParam;
import icu.nanshuo.constant.NumberConstant;
import icu.nanshuo.constant.VerifyParamRegexConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册 DTO
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23 19:00:34
 */
@Data
@ApiModel(value = "UserRegisterRequest", description = "用户注册信息请求DTO")
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -3801105286374526414L;

    /**
     * 用户名（必填）
     */
    @ApiModelProperty(value = "用户名", required = true)
    @CheckParam(alias = "用户名", regex = VerifyParamRegexConstant.USERNAME)
    private String userName;

    /**
     * 用户帐户（必填）
     */
    @ApiModelProperty(value = "账号", required = true)
    @CheckParam(alias = "账号", minLength = 3, maxLength = 11, regex = VerifyParamRegexConstant.ACCOUNT)
    private String userAccount;

    /**
     * 用户密码（必填）
     */
    @ApiModelProperty(value = "密码", required = true)
    @CheckParam(alias = "密码", minLength = 6, maxLength = 18, regex = VerifyParamRegexConstant.PASSWORD)
    private String userPassword;

    /**
     * 检查密码（必填）
     */
    @ApiModelProperty(value = "确认密码", required = true)
    @CheckParam(alias = "确认密码", minLength = 6, maxLength = 18, regex = VerifyParamRegexConstant.PASSWORD)
    private String checkPassword;

    /**
     * 电子邮件（非必填）
     */
    @ApiModelProperty(value = "邮箱", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "邮箱", regex = VerifyParamRegexConstant.EMAIL)
    private String email;

    /**
     * 电子邮件验证码（非必填）
     */
    @ApiModelProperty(value = "邮箱验证码", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "邮箱验证码", regex = VerifyParamRegexConstant.EMAIL_CAPTCHA)
    private String emailCaptcha;

    /**
     * 图像验证码（非必填）
     */
    @ApiModelProperty(value = "图片验证码", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "图片验证码", regex = VerifyParamRegexConstant.IMAGE_CAPTCHA)
    private String imageCaptcha;

    /**
     * 图像验证码密钥（非必填）
     */
    @ApiModelProperty(value = "图片验证码key", required = false)
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, alias = "图片验证码key")
    private String imageCaptchaKey;

}
