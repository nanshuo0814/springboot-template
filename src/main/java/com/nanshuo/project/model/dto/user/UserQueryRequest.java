package com.nanshuo.project.model.dto.user;

import com.nanshuo.project.model.dto.page.PageBaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询Request
 *
 * @author nanshuo
 * @date 2024/01/12 23:11:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageBaseRequest implements Serializable {

    private static final long serialVersionUID = -7808183174434904160L;

    /**
     * id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户性别
     */
    private Integer userGender;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

}
