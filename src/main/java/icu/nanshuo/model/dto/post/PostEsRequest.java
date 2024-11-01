package icu.nanshuo.model.dto.post;

import cn.hutool.core.collection.CollUtil;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.utils.JsonUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子 ES 包装类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
// todo 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "post")
@Data
@ApiModel(value = "PostEsRequest", description = "帖子 ES 包装类DTO")
public class PostEsRequest implements Serializable {

    /**
     * 日期时间模式
     */
    @ApiModelProperty(value = "日期时间格式", required = true)
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    @ApiModelProperty(value = "id", required = true)
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容", required = true)
    private String content;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表", required = true)
    private List<String> tags;

    /**
     * 点赞数
     */
    @ApiModelProperty(value = "点赞数", required = true)
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @ApiModelProperty(value = "收藏数", required = true)
    private Integer favourNum;

    /**
     * 创建人 id
     */
    @ApiModelProperty(value = "创建人", required = true)
    private Long createBy;

    /**
     * 更新人 id
     */
    @ApiModelProperty(value = "更新人", required = true)
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = true)
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", required = true)
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    @ApiModelProperty(value = "逻辑删除", required = true)
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param post post
     * @return {@code PostESRequest}
     */
    public static PostEsRequest objToDto(Post post) {
        if (post == null) {
            return null;
        }
        PostEsRequest postEsRequest = new PostEsRequest();
        BeanUtils.copyProperties(post, postEsRequest);
        String tagsStr = post.getTags();
        if (StringUtils.isNotBlank(tagsStr)) {
            postEsRequest.setTags(JsonUtils.jsonToList(tagsStr, String.class));
        }
        return postEsRequest;
    }

    /**
     * 包装类转对象
     *
     * @param postEsRequest post es请求
     * @return {@code Post}
     */
    public static Post dtoToObj(PostEsRequest postEsRequest) {
        if (postEsRequest == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEsRequest, post);
        List<String> tagList = postEsRequest.getTags();
        if (CollUtil.isNotEmpty(tagList)) {
            post.setTags(JsonUtils.objToJson(tagList));
        }
        return post;
    }
}
