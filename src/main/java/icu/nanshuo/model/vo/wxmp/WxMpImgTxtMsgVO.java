package icu.nanshuo.model.vo.wxmp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * 微信公众平台图文消息数据传输对象
 * 该类主要用于构建发送给微信服务器的图文消息数据
 *
 * @author @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxMpImgTxtMsgVO extends WxMpCommonMsgVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 图文消息的条目数量
     * 用于告诉微信服务器该消息包含的图文项数量
     */
    @XmlElement(name = "ArticleCount")
    private Integer articleCount;

    /**
     * 图文消息的具体内容项集合
     * 每个item代表一个图文消息项，包含标题、描述、图片链接等信息
     */
    @XmlElementWrapper(name = "Articles")
    @XmlElement(name = "item")
    private List<WxMpImgTxtItemVO> articles;

    /**
     * 默认构造函数
     * 初始化消息类型为"news"，即图文消息
     */
    public WxMpImgTxtMsgVO() {
        setMsgType("news");
    }
}

