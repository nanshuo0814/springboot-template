package ${packageName}.model.dto.${dataKey};

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建${dataName}请求
 *
 * @author ${author}
 * @Date ${date}
 */
@Data
@ApiModel(value = "${upperDataKey}AddRequest", description = "创建${dataName}请求")
public class ${upperDataKey}AddRequest implements Serializable {

    // todo 更多参数属性可自行添加

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
    * 标签列表
    */
    //@ApiModelProperty(value = "标签列表")
    //private List<String> tags;

    private static final long serialVersionUID = 1L;
}