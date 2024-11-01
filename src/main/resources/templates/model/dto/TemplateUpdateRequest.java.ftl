package ${packageName}.model.dto.${dataKey};

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新${dataName}请求
 *
 * @author <a href="https://github.com/nanshuo0814">南烁</a>
 */
@Data
@ApiModel(value = "${upperDataKey}AddRequest", description = "创建${dataName}请求")
public class ${upperDataKey}UpdateRequest implements Serializable {

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