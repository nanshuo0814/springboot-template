package com.nanshuo.springboot.constant;

/**
 * 页面常量
 *
 * @author 小鱼儿
 * @date 2024/01/11 21:08:48
 */
public interface PageConstant {

    /**
     * 当前页码（默认）
     */
   long CURRENT_PAGE = 1;

    /**
     * 页面大小（默认）
     */
    long PAGE_SIZE = 10;

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "asc";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "desc";

    /**
     * 默认排序字段
     */
    String USER_ID_SORT_FIELD = "userId";

    /**
     * 角色排序字段
     */
    String USER_ROLE_SORT_FIELD = "userRole";

    /**
     * 用户名排序字段
     */
    String USER_NAME_SORT_FIELD = "userName";

    /**
     * 用户性别排序字段
     */
    String USER_GENDER_SORT_FIELD = "userGender";

    /**
     * 用户邮箱排序字段
     */
    String USER_EMAIL_SORT_FIELD = "userEmail";

    /**
     * 用户帐户排序字段
     */
    String USER_ACCOUNT_SORT_FIELD = "userAccount";

    /**
     * 最新更新时间排序字段
     */
    String UPDATE_TIME_SORT_FIELD = "updateTime";

    /**
     * 创建时间排序字段
     */
    String CREATE_TIME_SORT_FIELD = "createTime";
}
