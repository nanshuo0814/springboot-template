package icu.nanshuo.model.dto.wxmp;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 微信公众平台文本消息传输类
 * 该类使用@Data注解自动生成getter和setter方法，以及toString、equals和hashCode方法
 * 使用@XmlRootElement注解指定该类为XML文档的根元素，名称为"xml"
 *
 * @author @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxMpTxtMsgRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 接收方账号（一个OpenID）
     */
    @XmlElement(name = "ToUserName")
    private String toUserName;

    /**
     * 发送方账号（一个OpenID）
     */
    @XmlElement(name = "FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @XmlElement(name = "CreateTime")
    private Long createTime;

    /**
     * 消息类型，此处固定为"text"
     */
    @XmlElement(name = "MsgType")
    private String msgType;

    /**
     * 事件类型，例如subscribe（订阅）、unsubscribe（取消订阅）等
     */
    @XmlElement(name = "Event")
    private String event;

    /**
     * 事件KEY值，用于区分特定事件
     */
    @XmlElement(name = "EventKey")
    private String eventKey;

    /**
     * 票据，用于二次验证
     */
    @XmlElement(name = "Ticket")
    private String ticket;

    /**
     * 文本消息内容
     */
    @XmlElement(name = "Content")
    private String content;

    /**
     * 消息ID，用于唯一标识一条消息
     */
    @XmlElement(name = "MsgId")
    private String msgId;

    /**
     * 消息数据ID
     */
    @XmlElement(name = "MsgDataId")
    private String msgDataId;

    /**
     * 消息索引，用于标识消息在批量发送中的位置
     */
    @XmlElement(name = "Idx")
    private String idx;
}

