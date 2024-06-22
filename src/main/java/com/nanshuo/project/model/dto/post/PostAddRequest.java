package com.nanshuo.project.model.dto.post;

import com.nanshuo.project.annotation.CheckParam;
import com.nanshuo.project.constant.NumberConstant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * post帖子添加请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31 12:02:53
 */
@Data
public class PostAddRequest implements Serializable {

    /**
     * 标题
     */
    @CheckParam(alias = "标题", maxLength = 80)
    private String title;

    /**
     * 内容
     */
    @CheckParam(alias = "内容", maxLength = 8192)
    private String content;

    /**
     * 标签列表
     */
    @CheckParam(alias = "标签", required = NumberConstant.FALSE_ZERO_VALUE)
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}