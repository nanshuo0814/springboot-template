package icu.nanshuo.model.vo.wxmp;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 微信公众号公共信息vo
 * 该类用于解析和封装微信服务器推送过来的公共消息
 *
 * @author @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxMpCommonMsgVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息接收方的用户名
     * 用于指定接收消息的微信用户或公众号
     */
    @XmlElement(name = "ToUserName")
    private String toUserName;

    /**
     * 消息发送方的用户名
     * 用于指定发送消息的微信用户或公众号
     */
    @XmlElement(name = "FromUserName")
    private String fromUserName;

    /**
     * 消息的创建时间
     * 用于记录消息的发送时间戳
     */
    @XmlElement(name = "CreateTime")
    private Long createTime;

    /**
     * 消息的类型
     * 用于区分不同的消息类型，如文本、图片、语音等
     */
    @XmlElement(name = "MsgType")
    private String msgType;
}

