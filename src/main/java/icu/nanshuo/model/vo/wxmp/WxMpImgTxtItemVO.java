package icu.nanshuo.model.vo.wxmp;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 微信图文项视图对象
 * 该类使用@Data注解，自动为所有字段生成getter和setter方法
 * 使用@XmlRootElement注解，指定该类为XML序列化的根元素
 *
 * @author 南烁
 * @date 2024/12/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxMpImgTxtItemVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标题
     * 使用@XmlElement注解，指定该字段在XML序列化时的元素名称
     */
    @XmlElement(name = "Title")
    private String title;

    /**
     * 描述
     * 使用@XmlElement注解，指定该字段在XML序列化时的元素名称
     */
    @XmlElement(name = "Description")
    private String description;

    /**
     * 图片URL
     * 使用@XmlElement注解，指定该字段在XML序列化时的元素名称
     */
    @XmlElement(name = "PicUrl")
    private String picUrl;

    /**
     * 链接URL
     * 使用@XmlElement注解，指定该字段在XML序列化时的元素名称
     */
    @XmlElement(name = "Url")
    private String url;
}

