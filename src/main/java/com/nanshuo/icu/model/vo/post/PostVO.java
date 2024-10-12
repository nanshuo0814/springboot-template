package com.nanshuo.icu.model.vo.post;

import com.nanshuo.icu.model.domain.Post;
import com.nanshuo.icu.model.vo.user.UserVO;
import com.nanshuo.icu.utils.JsonUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Data
@ApiModel(value = "PostVO", description = "帖子视图VO")
public class PostVO implements Serializable {

    /**
     * id
     */
    @ApiModelProperty(value = "帖子id", required = true)
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "帖子标题", required = true)
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty(value = "帖子内容", required = true)
    private String content;

    /**
     * 点赞数
     */
    @ApiModelProperty(value = "帖子点赞数", required = true)
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @ApiModelProperty(value = "帖子收藏数", required = true)
    private Integer favourNum;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "帖子创建人id", required = true)
    private Long createBy;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "帖子更新人id", required = true)
    private Long updateBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "帖子创建时间", required = true)
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "帖子更新时间", required = true)
    private Date updateTime;

    /**
     * 标签列表
     */
    @ApiModelProperty(value = "帖子标签", required = true)
    private List<String> tagList;

    /**
     * 创建人信息
     */
    @ApiModelProperty(value = "帖子创建人信息", required = true)
    private UserVO user;

    /**
     * 是否已点赞
     */
    @ApiModelProperty(value = "是否已点赞", required = true)
    private Boolean hasThumb;

    /**
     * 是否已收藏
     */
    @ApiModelProperty(value = "是否已收藏", required = true)
    private Boolean hasFavour;

    /**
     * 包装类转对象
     *
     * @param postVO post vo
     * @return {@code Post}
     */
    public static Post voToObj(PostVO postVO) {
        if (postVO == null) {
            return null;
        }
        Post post = new Post();
        BeanUtils.copyProperties(postVO, post);
        List<String> tagList = postVO.getTagList();
        post.setTags(JsonUtils.objToJson(tagList));
        return post;
    }

    /**
     * 对象转包装类
     *
     * @param post post
     * @return {@code PostVO}
     */
    public static PostVO objToVo(Post post) {
        if (post == null) {
            return null;
        }
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        postVO.setTagList(JsonUtils.jsonToList(post.getTags(), String.class));
        return postVO;
    }
}
