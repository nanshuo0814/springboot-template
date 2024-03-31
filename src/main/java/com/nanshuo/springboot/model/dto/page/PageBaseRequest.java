package com.nanshuo.springboot.model.dto.page;

import com.nanshuo.springboot.constant.PageConstant;
import lombok.Data;

/**
 * 页面请求
 *
 * @author nanshuo
 * @date 2024/01/11 21:07:14
 */
@Data
public class PageBaseRequest {

    /**
     * 当前页号
     */
    private long current = PageConstant.CURRENT_PAGE;

    /**
     * 页面大小
     */
    private long pageSize = PageConstant.PAGE_SIZE;

    /**
     * 排序字段(默认ID)
     */
    private String sortField = PageConstant.SORT_BY_ID;

    /**
     * 排序顺序（默认升序ASC）
     */
    private String sortOrder = PageConstant.SORT_ORDER_DESC;

}