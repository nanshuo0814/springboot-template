package com.nanshuo.springboot.model.dto.post;

import cn.hutool.core.collection.CollUtil;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.utils.JsonUtils;
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
 * @author nanshuo
 * @date 2024/03/31 22:32:24
 */
// todo 取消注释开启 ES（须先配置 ES）
//@Document(indexName = "post")
@Data
public class PostEsRequest implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

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

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param post post
     * @return {@code PostEsRequest}
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
