package icu.nanshuo.config;

import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 微信公众号配置
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/14
 */
@Configuration
public class WxMpConfig {

    @Resource
    private WxMpService wxMpService;

    /**
     * 微信公众号消息路由器
     *
     * @return {@link WxMpMessageRouter }
     */
    @Bean
    public WxMpMessageRouter wxMpMessageRouter() {
        return new WxMpMessageRouter(wxMpService);
    }
}
