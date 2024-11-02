package icu.nanshuo.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子点赞
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="post_praise")
@Data
public class PostPraise extends CommonBaseEntity implements Serializable {

    /**
     * 帖子 id
     */
    @TableField(value = "post_id")
    @ApiModelProperty(value = "帖子id")
    private Long postId;

    /**
     * 是否删除，0:默认，1:删除
     */
    @TableField(exist = false)
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}