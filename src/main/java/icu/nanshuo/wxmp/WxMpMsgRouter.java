package icu.nanshuo.wxmp;

import icu.nanshuo.wxmp.handler.BottomUpHandler;
import icu.nanshuo.wxmp.handler.EventHandler;
import icu.nanshuo.wxmp.handler.MessageHandler;
import icu.nanshuo.wxmp.handler.SubscribeHandler;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 微信公众号路由
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/17
 */
@Configuration
public class WxMpMsgRouter {

    @Resource
    private WxMpService wxMpService;
    @Resource
    private EventHandler eventHandler;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private SubscribeHandler subscribeHandler;
    @Resource
    private BottomUpHandler bottomUpHandler;

    @Bean
    public WxMpMessageRouter getWxMsgRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 事件相关
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE) // 关注订阅公众号
                .handler(subscribeHandler) // 进入关注订阅处理
                .end();
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.CLICK) // 其他事件
                .handler(eventHandler) // 进入事件处理
                .end();
        // 消息文本相关
        router.rule()
                .async(false)
                .msgType(XmlMsgType.TEXT)
                .handler(messageHandler)
                .end();
        // 兜底
        router.rule()
                .async(false)
                .handler(bottomUpHandler)
                .end();
        return router;
    }
}
