package icu.nanshuo.model.vo.wxmp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


/**
 * 微信公众平台文本消息响应视图对象
 * 该类继承自WxMpCommonMsgVO，用于封装文本消息的响应数据
 * 主要添加了内容字段，并设置了消息类型为文本消息
 *
 * @author @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxMpTxtMsgVO extends WxMpCommonMsgVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息内容
     * 用于存储和展示文本消息的具体内容
     */
    @XmlElement(name = "Content")
    private String content;

    /**
     * 默认构造函数
     * 初始化时设置消息类型为文本消息（"text"）
     */
    public WxMpTxtMsgVO() {
        setMsgType("text");
    }
}

