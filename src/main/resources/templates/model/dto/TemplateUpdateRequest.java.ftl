package ${packageName}.model.dto.${dataKey};

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新${dataName}请求
 *
 * @author ${author}
 * @Date ${date}
 */
@Data
@ApiModel(value = "${upperDataKey}UpdateRequest", description = "更新${dataName}请求")
public class ${upperDataKey}UpdateRequest implements Serializable {

    // todo 更多参数属性可自行添加

    /**
     * id
     */
    @ApiModelProperty(example = "1", value = "id")
    private Long id;

    /**
    * 创建者
    */
    @ApiModelProperty(example = "1", value = "创建者")
    private Long createBy;

    /**
    * 标签列表
    */
    //@ApiModelProperty(example = "['java','c']", value = "标签列表")
    //private List<String> tags;

    private static final long serialVersionUID = 1L;
}