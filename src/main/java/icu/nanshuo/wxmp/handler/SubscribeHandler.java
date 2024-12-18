package icu.nanshuo.wxmp.handler;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 关注订阅处理程序
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/17
 */
@Component
public class SubscribeHandler implements WxMpMessageHandler {

    @Value("${wechat.official.account.wxMpName}")
    private String wxMpName;
    @Value("${wechat.official.account.email}")
    private String email;
    @Value("${wechat.official.account.qq}")
    private String QQ;
    @Value("${wechat.official.account.weixin}")
    private String wechat;
    @Value("${wechat.official.account.website}")
    private String website;
    @Value("${wechat.official.account.github}")
    private String github;
    @Value("${wechat.official.account.dynamicCode}")
    private String dynamicCode;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        final String content = "\uD83C\uDF89 叮咚～你终于来啦！\n" +
                "\uD83C\uDF3F欢迎关注【" + wxMpName + "】公众号！\n" +
                "✨这里是一个充满干货、灵感与趣味的天地！\n" +
                "\uD83D\uDC40 新朋友，不知道从哪开始？ 点击菜单栏看看你感兴趣的内容，或直接回复关键词，让我带你入门！\n" +
                "\uD83D\uDCDA让我们一起玩转这个知识宝库吧！\n" +
                "❤\uFE0F期待与你一起探索更多有趣、有价值的内容！\n" +
                "\uD83D\uDCAA再次感谢关注，让我们一起进步吧！\n" +
                "\uD83C\uDF38站长相关信息如下：\n" +
                "\uD83D\uDCAB微信号：" + wechat + "\n" +
                "\uD83D\uDC8C邮箱号：" + email + "\n" +
                "\uD83D\uDC27QQ号：" + QQ + "\n" +
                "\uD83D\uDE80网站：<a href=\"" + website + "\">" + website + "</a>\n" +
                "\uD83D\uDCD6github：<a href=\"" + github + "\">" + github + "</a>\n" +
                "\uD83C\uDF1F备注：\n1、网站微信登录的动态码查看，请回复“" + dynamicCode + "”关键词获取\n" +
                "2、获取站长微信二维码，请回复“666”\n" +
                "3、获取微信公众号二维码，请回复“888”";
        // 调用接口，返回验证码
        return WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser())
                .build();
    }

}
