package icu.nanshuo.wxmp.handler;

import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Map;

import static icu.nanshuo.wxmp.WxMpConstant.*;

/**
 * 事件处理程序
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/17
 */
@Slf4j
@Component
public class EventHandler implements WxMpMessageHandler {

    @Value("${wechat.official.account.website}")
    private String website;
    @Value("${wechat.official.account.description}")
    private String description;
    @Value("${wechat.official.account.logo}")
    private String logo;

    // 随机数生成器
    private static final SecureRandom RANDOM = new SecureRandom();
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;
    @Resource
    private PublicMethods publicMethods;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        // 获取事件 key
        String content = wxMpXmlMessage.getEventKey();
        // 判断 key 事件类型
        String codeStr = null; // 验证码
        WxMpXmlOutNewsMessage.Item item = null; // 图文卡片
        String mediaId = null;
        switch (content) {
            case BIND_CODE_KEY:
                codeStr = publicMethods.generateBindCode(wxMpXmlMessage.getFromUser());
                break;
            case DYNAMIC_CODE_KEY:
                codeStr = publicMethods.generateDynamicCode(wxMpXmlMessage.getFromUser());
                break;
            case WX_QR_CODE_KEY:
                mediaId = WX_QR_CODE_FILE_NAME;
                log.info("openId: {},获取微信二维码", wxMpXmlMessage.getFromUser());
                break;
            case WX_MP_QR_CODE_KEY:
                mediaId = WX_MP_CODE_MEDIA_ID;
                log.info("openId: {},获取微信公众号二维码", wxMpXmlMessage.getFromUser());
                break;
            case WEBSITE_CARD_KEY:
                item = setupItem("站长个人网站（\" + wxMpName + \"）", description, logo, website);
                log.info("openId: {},获取网站链接", wxMpXmlMessage.getFromUser());
                break;
            default:
                codeStr = "发送什么事啦(O_o)??获取触发事件失败，未知错误";
                break;
        }
        if (item != null) {
            return WxMpXmlOutMessage.NEWS()
                    .addArticle(item)
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        } else if (mediaId != null) {
            return WxMpXmlOutMessage.IMAGE().mediaId(mediaId)
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        } else {
            return WxMpXmlOutMessage.TEXT().content(codeStr)
                    .fromUser(wxMpXmlMessage.getToUser())
                    .toUser(wxMpXmlMessage.getFromUser())
                    .build();
        }
    }

    /**
     * 设置项目
     *
     * @param title       标题
     * @param description 描述
     * @param picUrl      pic url
     * @param url         url
     * @return {@link WxMpXmlOutNewsMessage.Item }
     */
    private WxMpXmlOutNewsMessage.Item setupItem(String title, String description, String picUrl, String url) {
        WxMpXmlOutNewsMessage.Item imgTxt = new WxMpXmlOutNewsMessage.Item();
        imgTxt.setTitle(title);
        imgTxt.setDescription(description);
        imgTxt.setPicUrl(picUrl);
        imgTxt.setUrl(url);
        return imgTxt;
    }

}
