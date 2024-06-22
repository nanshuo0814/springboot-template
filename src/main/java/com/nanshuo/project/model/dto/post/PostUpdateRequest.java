package com.nanshuo.project.model.dto.post;

import com.nanshuo.project.annotation.CheckParam;
import com.nanshuo.project.constant.NumberConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @author nanshuo
 * @date 2024/03/31 14:27:37
 */
@Data
public class PostUpdateRequest implements Serializable {

    /**
     * id
     */
    @CheckParam(alias = "id", minValue = 0)
    private Long id;

    /**
     * 标题
     */
    @CheckParam(alias = "标题", maxLength = 80, required = NumberConstant.FALSE_ZERO_VALUE)
    private String title;

    /**
     * 内容
     */
    @CheckParam(alias = "内容", maxLength = 8192, required = NumberConstant.FALSE_ZERO_VALUE)
    private String content;

    /**
     * 标签列表
     */
    @CheckParam(alias = "标签", required = NumberConstant.FALSE_ZERO_VALUE)
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}