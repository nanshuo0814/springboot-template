package icu.nanshuo.model.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子更新请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Data
@ApiModel(value = "PostUpdateRequest", description = "帖子更新请求DTO")
public class PostUpdateRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}