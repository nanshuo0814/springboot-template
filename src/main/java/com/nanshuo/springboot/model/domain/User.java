package com.nanshuo.springboot.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 *
 * @author nanshuo
 * @TableName user
 * @date 2024/01/04 14:43:30
 */
@Data
@TableName(value ="user")
@ApiModel(value = "User", description = "用户实体类")
public class User implements Serializable {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    @TableField(value = "user_name")
    private String userName;

    /**
     * 0-女，1-男，2-未知
     */
    @ApiModelProperty(value = "0-女，1-男，2-未知")
    @TableField(value = "user_gender")
    private Integer userGender;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @TableField(value = "user_email")
    private String userEmail;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @TableField(value = "user_avatar")
    private String userAvatar;

    /**
     * 用户简介
     */
    @ApiModelProperty(value = "用户简介")
    @TableField(value = "user_profile")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @ApiModelProperty(value = "用户角色：user/admin/ban")
    @TableField(value = "user_role")
    private String userRole;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除，0:默认，1:删除
     */
    @ApiModelProperty(value = "是否删除，0:默认，1:删除")
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}