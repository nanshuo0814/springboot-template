package icu.nanshuo.model.dto.post;

import icu.nanshuo.model.dto.page.PageBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子查询请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "PostQueryRequest", description = "帖子查询请求DTO")
public class PostQueryRequest extends PageBaseRequest implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 排除某个帖子的id，在其余的帖子id里查询
     */
    @ApiModelProperty(value = "排除某个帖子的id，在其余的帖子id里查询")
    private Long notId;

    /**
     * 搜索词
     */
    @ApiModelProperty(value = "搜索词")
    private String searchText;

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

    /**
     * 至少有某任意一个标签
     */
    @ApiModelProperty(value = "至少有某任意一个标签")
    private List<String> orTags;

    /**
     * 创建人 id
     */
    @ApiModelProperty(value = "创建人id")
    private Long createBy;

    private static final long serialVersionUID = 1L;
}