package ${packageName}.model.vo;

import cn.hutool.json.JSONUtil;
import ${packageName}.model.domain.${upperDataKey};
import ${packageName}.model.vo.user.UserVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ${dataName}视图
 *
 * @author ${author}
 * @Date ${date}
 */
@Data
@ApiModel(value = "${upperDataKey}AddRequest", description = "创建${dataName}请求")
public class ${upperDataKey}VO implements Serializable {

    // 更多参数属性可自行添加

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
     * 创建用户 id
     */
    @ApiModelProperty(value = "创建用户 id")
    private Long createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 创建用户信息
     */
    @ApiModelProperty(value = "创建用户信息")
    private UserVO user;

    /**
    * 标签列表
    */
    //@ApiModelProperty(value = "标签列表")
    //private List<String> tags;

    /**
     * 封装类转对象
     *
     * @param ${dataKey}VO
     * @return
     */
    public static ${upperDataKey} voToObj(${upperDataKey}VO ${dataKey}VO) {
        if (${dataKey}VO == null) {
            return null;
        }
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}VO, ${dataKey});
        //List<String> tagList = ${dataKey}VO.getTagList();
        //${dataKey}.setTags(JSONUtil.toJsonStr(tagList));
        return ${dataKey};
    }

    /**
     * 对象转封装类
     *
     * @param ${dataKey}
     * @return
     */
    public static ${upperDataKey}VO objToVo(${upperDataKey} ${dataKey}) {
        if (${dataKey} == null) {
            return null;
        }
        ${upperDataKey}VO ${dataKey}VO = new ${upperDataKey}VO();
        BeanUtils.copyProperties(${dataKey}, ${dataKey}VO);
        //${dataKey}VO.setTagList(JSONUtil.toList(${dataKey}.getTags(), String.class));
        return ${dataKey}VO;
    }
}
