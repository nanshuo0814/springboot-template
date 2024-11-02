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
@ApiModel(value = "${upperDataKey}AddRequest", description = "创建${dataName}请求")
public class ${upperDataKey}UpdateRequest implements Serializable {

    // todo 更多参数属性可自行添加

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 创建者
    */
    @ApiModelProperty(value = "创建者")
    private Long createBy;

    /**
    * 标签列表
    */
    //@ApiModelProperty(value = "标签列表")
    //private List<String> tags;

    private static final long serialVersionUID = 1L;
}