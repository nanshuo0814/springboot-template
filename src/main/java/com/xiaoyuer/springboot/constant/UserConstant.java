package com.xiaoyuer.springboot.constant;

/**
 * 用户常量
 *
 * @author 小鱼儿
 * @date 2024/01/03 19:33:58
 */
public interface UserConstant {


    /**
     * 盐值，混淆密码
     */
    String SALT = "ydg0814";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

}
