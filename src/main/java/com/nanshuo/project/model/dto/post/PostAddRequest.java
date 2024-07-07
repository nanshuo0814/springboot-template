package com.nanshuo.project.model.dto.post;

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
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}