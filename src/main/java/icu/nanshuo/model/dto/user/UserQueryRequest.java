package icu.nanshuo.model.dto.user;

import icu.nanshuo.annotation.CheckParam;
import icu.nanshuo.constant.NumberConstant;
import icu.nanshuo.constant.VerifyParamRegexConstant;
import icu.nanshuo.model.dto.page.PageBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询Request
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/12 23:11:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "UserQueryRequest", description = "用户查询请求DTO")
public class UserQueryRequest extends PageBaseRequest implements Serializable {

    private static final long serialVersionUID = -7808183174434904160L;

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.ID, alias = "id")
    private Long id;

    /**
     * 用户账号
     */
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.ACCOUNT, alias = "账号")
    @ApiModelProperty(value = "用户账号")
    private String userAccount;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USERNAME, alias = "用户名")
    private String userName;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USER_GENDER, alias = "性别")
    private Integer userGender;

    /**
     * 用户邮箱
     */
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.EMAIL, alias = "邮箱")
    @ApiModelProperty(value = "用户邮箱")
    private String userEmail;

    /**
     * 简介
     */
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, maxLength = 512, alias = "用户简介")
    @ApiModelProperty(value = "用户简介")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色（user/admin/ban）")
    @CheckParam(required = NumberConstant.FALSE_ZERO_VALUE, regex = VerifyParamRegexConstant.USER_ROLE, alias = "用户权限")
    private String userRole;

}
