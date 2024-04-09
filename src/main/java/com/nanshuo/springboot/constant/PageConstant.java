package com.nanshuo.springboot.constant;

/**
 * 页面常量
 *
 * @author nanshuo
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
     * 默认按id排序
     */
    String SORT_BY_ID = "id";
}
