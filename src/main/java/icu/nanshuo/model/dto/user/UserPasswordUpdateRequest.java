package icu.nanshuo.model.dto.user;

import icu.nanshuo.annotation.CheckParam;
import icu.nanshuo.constant.VerifyParamRegexConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码更新Request
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/04 22:50:31
 */
@Data
@ApiModel(value = "UserPasswordUpdateRequest", description = "用户密码更新信息请求DTO")
public class UserPasswordUpdateRequest implements Serializable {

    private static final long serialVersionUID = 8383202174723157092L;

    @ApiModelProperty(value = "原密码", required = true)
    @CheckParam(alias = "原密码", regex = VerifyParamRegexConstant.PASSWORD)
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @CheckParam(alias = "新密码", minLength = 6, maxLength = 18,regex = VerifyParamRegexConstant.PASSWORD)
    private String newPassword;

    @ApiModelProperty(value = "确认密码", required = true)
    @CheckParam(alias = "确认密码", minLength = 6, maxLength = 18,regex = VerifyParamRegexConstant.PASSWORD)
    private String checkPassword;

}
