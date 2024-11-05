package ${packageName}.model.dto.${dataKey};

import ${packageName}.model.dto.page.PageBaseRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询${dataName}请求
 *
 * @author ${author}
 * @Date ${date}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "${upperDataKey}QueryRequest", description = "查询${dataName}请求")
public class ${upperDataKey}QueryRequest extends PageBaseRequest implements Serializable {

   // todo 更多参数属性可自行添加

    /**
     * id
     */
    @ApiModelProperty(example = "1", value = "id")
    private Long id;

    /**
     * id
     */
    @ApiModelProperty(example = "2", value = "不包含的id")
    private Long notId;

    /**
     * 搜索词
     */
    @ApiModelProperty(example = "java", value = "搜索词")
    private String searchText;

    /**
     * 标题
     */
    @ApiModelProperty(example = "这是一个标题", value = "标题")
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty(example = "这是一段内容", value = "内容")
    private String content;

    /**
     * 创建用户 id
     */
    @ApiModelProperty(example = "1", value = "创建用户 id")
    private Long createBy;

    /**
    * 标签列表
    */
    //@ApiModelProperty(example = "['java','c']", value = "标签列表")
    //private List<String> tags;

    private static final long serialVersionUID = 1L;
}