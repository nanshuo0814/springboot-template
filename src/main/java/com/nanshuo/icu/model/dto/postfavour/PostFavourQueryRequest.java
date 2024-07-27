package com.nanshuo.icu.model.dto.postfavour;

import com.nanshuo.icu.model.dto.page.PageBaseRequest;
import com.nanshuo.icu.model.dto.post.PostQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子收藏查询请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PostFavourQueryRequest", description = "帖子收藏查询请求DTO")
public class PostFavourQueryRequest extends PageBaseRequest implements Serializable {

    /**
     * 帖子查询请求
     */
    @ApiModelProperty(value = "帖子查询请求DTO", required = true)
    private PostQueryRequest postQueryRequest;

    /**
     * 创建人 id
     */
    @ApiModelProperty(value = "创建人id", required = true)
    private Long createBy;

    private static final long serialVersionUID = 1L;
}