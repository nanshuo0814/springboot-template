package com.nanshuo.project.model.dto.user;

import com.nanshuo.project.annotation.CheckParam;
import com.nanshuo.project.model.enums.user.UserRegexEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户重置pwd请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/06/25
 */
@Data
@ApiModel(value = "UserResetPwdRequest", description = "用户重置密码Request")
public class UserResetPwdRequest implements Serializable {

    private static final long serialVersionUID = -8011153806807323196L;

    @ApiModelProperty(value = "邮箱", required = true)
    @CheckParam(alias = "邮箱", regex = UserRegexEnums.EMAIL)
    private String email;

    @ApiModelProperty(value = "新密码", required = true)
    @CheckParam(alias = "用户新密码", minLength = 6, maxLength = 18,regex = UserRegexEnums.PASSWORD)
    private String newPassword;

    @ApiModelProperty(value = "确认密码", required = true)
    @CheckParam(alias = "确认密码", minLength = 6, maxLength = 18, regex = UserRegexEnums.PASSWORD)
    private String confirmPassword;

    @ApiModelProperty(value = "校验邮箱验证码成功的凭证", required = true)
    @CheckParam(alias = "校验邮箱验证码成功的凭证")
    private String voucher;

}
