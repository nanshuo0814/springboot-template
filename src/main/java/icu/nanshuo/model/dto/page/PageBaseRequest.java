package icu.nanshuo.model.dto.page;

import icu.nanshuo.constant.PageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 页面基本请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Data
@ApiModel(value = "PageBaseRequest", description = "分页基本请求DTO")
public class PageBaseRequest {

    /**
     * 当前页号
     */
    @ApiModelProperty(value = "当前页号", required = true)
    private long current = PageConstant.CURRENT_PAGE;

    /**
     * 页面大小
     */
    @ApiModelProperty(value = "当前页数量", required = true)
    private long pageSize = PageConstant.PAGE_SIZE;

    /**
     * 排序字段(默认ID)
     */
    @ApiModelProperty(value = "排序字段", required = true)
    private String sortField = PageConstant.SORT_BY_ID;

    /**
     * 排序顺序（默认升序ASC）
     */
    @ApiModelProperty(value = "排序顺序（升降序）", required = true)
    private String sortOrder = PageConstant.SORT_ORDER_DESC;

}