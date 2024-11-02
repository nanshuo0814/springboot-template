package icu.nanshuo.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="post")
@Data
public class Post extends CommonBaseEntity implements Serializable {

    /**
     * 标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "帖子标题")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "帖子内容")
    private String content;

    /**
     * 标签列表（json 数组）
     */
    @TableField(value = "tags")
    @ApiModelProperty(value = "帖子标签")
    private String tags;

    /**
     * 点赞数
     */
    @TableField(value = "praise_num")
    @ApiModelProperty(value = "帖子点赞数")
    private Integer praiseNum;

    /**
     * 收藏数
     */
    @TableField(value = "collect_num")
    @ApiModelProperty(value = "帖子收藏数")
    private Integer collectNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}