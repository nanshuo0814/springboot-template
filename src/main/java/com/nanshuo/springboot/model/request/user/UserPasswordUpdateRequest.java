package com.nanshuo.springboot.model.request.user;

import com.nanshuo.springboot.annotation.CheckParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户密码更新Request
 *
 * @author nanshuo
 * @date 2024/01/04 22:50:31
 */
@Data
@ApiModel(value = "UserPasswordUpdateRequest", description = "用户密码更新信息Request")
public class UserPasswordUpdateRequest implements Serializable {

    private static final long serialVersionUID = 8383202174723157092L;

    @ApiModelProperty(value = "原密码", required = true)
    @CheckParam(nullErrorMsg = "原密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "原密码长度必须在6-18之间")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @CheckParam(nullErrorMsg = "新密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "新密码长度必须在6-18之间")
    private String newPassword;

    @ApiModelProperty(value = "第二遍输入的新密码", required = true)
    @CheckParam(nullErrorMsg = "第二遍输入的新密码不能为空", minLength = 6, maxLength = 18, lenghtErrorMsg = "第二遍输入的新密码长度必须在6-18之间")
    private String checkPassword;

}
