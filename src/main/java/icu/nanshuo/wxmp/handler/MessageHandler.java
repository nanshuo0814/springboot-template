package icu.nanshuo.wxmp.handler;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static icu.nanshuo.wxmp.WxMpConstant.WX_MP_CODE_MEDIA_ID;
import static icu.nanshuo.wxmp.WxMpConstant.WX_QR_CODE_FILE_NAME;

/**
 * 消息处理程序
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/17
 */
@Slf4j
@Component
public class MessageHandler implements WxMpMessageHandler {

    @Resource
    private PublicMethods publicMethods;
    @Value("${wechat.official.account.dynamicCode}")
    private String dynamicCode;
    @Value("${wechat.official.account.bindCode}")
    private String bindCode;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 获取事件 key
        String content = wxMpXmlMessage.getContent();
        // 判断消息类型
        if (content.equals("666")) {
            return WxMpXmlOutMessage.IMAGE().mediaId(WX_QR_CODE_FILE_NAME)
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        } else if (content.equals("888")) {
            return WxMpXmlOutMessage.IMAGE().mediaId(WX_MP_CODE_MEDIA_ID)
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        }
        String result = "";
        if (content.equals(bindCode)) {
            result = publicMethods.generateBindCode(wxMpXmlMessage.getFromUser());
        } else if (content.equals(dynamicCode)) {
            result = publicMethods.generateDynamicCode(wxMpXmlMessage.getFromUser());
        } else {
            log.info("openId: {},未识别的消息", wxMpXmlMessage.getFromUser());
            result = "发送什么事啦(O_o)??\n自动回复：你似乎回复了无法识别的消息，导致迷失了方向鸭？这里什么都没有。。。";
        }
        return WxMpXmlOutMessage.TEXT().content(result)
                .fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser())
                .build();
    }

}
