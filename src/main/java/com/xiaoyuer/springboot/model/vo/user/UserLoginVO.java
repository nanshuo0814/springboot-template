package com.xiaoyuer.springboot.model.vo.user;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录vo
 *
 * @author 小鱼儿
 * @date 2024/01/04 19:08:31
 */
@Data
public class UserLoginVO implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户邮箱
     */
    private String userEmail;


    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}