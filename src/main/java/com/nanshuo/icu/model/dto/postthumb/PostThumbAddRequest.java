package com.nanshuo.icu.model.dto.postthumb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞添加请求DTO
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Data
@ApiModel(value = "PostThumbAddRequest", description = "帖子点赞添加请求DTO")
public class PostThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    @ApiModelProperty(value = "帖子id", required = true)
    private Long postId;

    private static final long serialVersionUID = 1L;
}