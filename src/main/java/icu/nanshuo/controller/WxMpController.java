package icu.nanshuo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import icu.nanshuo.common.ApiResponse;
import icu.nanshuo.common.ApiResult;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.constant.RedisKeyConstant;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.user.UserAddRequest;
import icu.nanshuo.model.vo.user.UserLoginVO;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.ThrowUtils;
import icu.nanshuo.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static icu.nanshuo.constant.UserConstant.USER_LOGIN_STATE;
import static icu.nanshuo.constant.WxMpConstant.WX_MP_BIND_DYNAMIC_CODE;
import static icu.nanshuo.constant.WxMpConstant.WX_MP_LOGIN_DYNAMIC_CODE;
import static icu.nanshuo.wxmp.WxMpConstant.*;

/**
 * 微信公众号相关接口
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/18
 */
@RestController
@RequestMapping("/wx/mp")
@Slf4j
public class WxMpController {

    @Resource
    private WxMpService wxMpService;
    @Resource
    private WxMpMessageRouter wxMpMessageRouter;
    @Resource
    private WxMpConfigStorage wxMpConfigStorage;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;
    @Value("${superAdmin.email}")
    private String adminEmail;

    /**
     * 微信的公众号接入 token 验证，即返回 echostr 的参数值
     *
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param signature 签名
     * @param echostr   echostr
     * @return {@link String }
     */
    @GetMapping("/callback")
    public String check(String timestamp, String nonce, String signature, String echostr) {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.info("callback check success，echostr: {}", echostr);
            return echostr;
        } else {
            log.error("callback check error");
            return "非法请求ya~";
        }
    }

    /**
     * 接收消息
     *
     * @param requestBody 请求正文
     * @param request     请求
     * @return {@link String }
     * @throws IOException ioexception
     */
    @PostMapping(path = "/callback", consumes = {"application/xml", "text/xml"}, produces = "application/xml;charset=utf-8")
    public String receiveMessage(@RequestBody String requestBody, HttpServletRequest request) throws IOException {
        // 校验消息签名，判断是否为公众平台发的消息
        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        // 判断消息是否是加密的
        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ? "raw" : request.getParameter("encrypt_type");
        WxMpXmlMessage inMessage = null;
        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            inMessage = WxMpXmlMessage.fromXml(requestBody);
        } else if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpConfigStorage, timestamp, nonce, msgSignature);
        } else {
            log.error("加密类型：{}暂不支持！", encryptType);
            throw new IllegalArgumentException("加密类型：" + encryptType + "暂不支持！");
        }
        // 路由转发
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
        if (outMessage != null) {
            if ("raw".equals(encryptType)) {
                // 明文
                return outMessage.toXml();
            } else {
                // aes加密
                return outMessage.toEncryptedXml(wxMpConfigStorage);
            }
        }
        log.error("发生什么事啦(O_o)??消息：{}，没有对应的处理程序！", inMessage);
        return "发生什么事啦(O_o)??";
    }

    /**
     * 设置公众号菜单（至少需要服务号才有权限设置，目前个人也可以申请一个服务号了）
     *
     * @return {@link String }
     * @throws WxErrorException wx错误异常
     */
    @GetMapping("/setMenu")
    public String setMenu() throws WxErrorException {
        log.info("设置公众号菜单");
        WxMenu wxMenu = new WxMenu();

        // todo 微信公众号菜单自定义设置
        // 菜单一
        WxMenuButton menu1 = new WxMenuButton();
        menu1.setType(MenuButtonType.VIEW);
        menu1.setName("服务内容");
        // 菜单一下的子菜单
        // 子菜单1: 获取网站登录动态码
        WxMenuButton menu1button1 = new WxMenuButton();
        menu1button1.setType(MenuButtonType.CLICK);
        menu1button1.setName("获取动态码");
        menu1button1.setKey(DYNAMIC_CODE_KEY);
        // 子菜单2: 小程序跳转
        //WxMenuButton menu1button2 = new WxMenuButton();
        //menu1button2.setType(MenuButtonType.MINIPROGRAM);
        //menu1button2.setName("小程序");
        //menu1button2.setUrl("xxx");
        //menu1button2.setAppId("xxx");
        //menu1button2.setPagePath("/pages/index/index");
        // 子菜单2：获取绑定码
        WxMenuButton menu1button2 = new WxMenuButton();
        menu1button2.setType(MenuButtonType.CLICK);
        menu1button2.setName("获取绑定码");
        menu1button2.setKey(BIND_CODE_KEY);
        // 子菜单3：随机美句事件
        WxMenuButton menu1button3 = new WxMenuButton();
        menu1button3.setType(MenuButtonType.VIEW);
        menu1button3.setName("毒鸡汤来喽");
        menu1button3.setUrl("https://btstu.cn/yan/api.php?charset=utf-8&encode=text");
        // 子菜单4：Bing 每日一图
        WxMenuButton menu1button4 = new WxMenuButton();
        menu1button4.setType(MenuButtonType.VIEW);
        menu1button4.setName("Bing今日壁纸");
        menu1button4.setUrl("https://todayimg.nanshuo.icu");
        menu1.setSubButtons(Arrays.asList(menu1button1, menu1button2, menu1button3, menu1button4));

        // 菜单二
        WxMenuButton menu2 = new WxMenuButton();
        menu2.setName("烁烁南光");
        menu2.setType(MenuButtonType.VIEW);
        menu2.setUrl("https://nanshuo.icu");

        // 菜单三
        WxMenuButton menu3 = new WxMenuButton();
        menu3.setType(MenuButtonType.VIEW);
        menu3.setName("关于站长");
        // 子菜单1: 跳转个人网站
        WxMenuButton menu3button1 = new WxMenuButton();
        menu3button1.setType(MenuButtonType.VIEW);
        menu3button1.setName("个人网站");
        menu3button1.setUrl("https://nanshuo.icu");
        // 子菜单2: 跳转github
        WxMenuButton menu3button2 = new WxMenuButton();
        menu3button2.setType(MenuButtonType.VIEW);
        menu3button2.setName("github");
        menu3button2.setUrl("https://github.com/nanshuo0814");
        // 子菜单3: 跳转CSDN
        WxMenuButton menu3button3 = new WxMenuButton();
        menu3button3.setType(MenuButtonType.VIEW);
        menu3button3.setName("CSDN");
        menu3button3.setUrl("https://blog.csdn.net/Yuan_Master?spm=1000.2115.3001.5343");
        // 子菜单4：微信二维码
        WxMenuButton menu3button4 = new WxMenuButton();
        menu3button4.setType(MenuButtonType.CLICK);
        menu3button4.setName("微信二维码");
        menu3button4.setKey(WX_QR_CODE_KEY);
        // 子菜单5：微信公众号二维码
        WxMenuButton menu3button5 = new WxMenuButton();
        menu3button5.setType(MenuButtonType.CLICK);
        menu3button5.setName("公众号二维码");
        menu3button5.setKey(WX_MP_QR_CODE_KEY);
        menu3.setSubButtons(Arrays.asList(menu3button1, menu3button2, menu3button3, menu3button4, menu3button5));

        // 设置主菜单
        wxMenu.setButtons(Arrays.asList(menu1, menu2, menu3));
        wxMpService.getMenuService().menuCreate(wxMenu);
        return "成功设置公众号菜单";
    }

    /**
     * 微信公众号登录
     *
     * @param code 动态码
     * @return {@link String }
     */
    @PostMapping("/login")
    public ApiResponse<UserLoginVO> login(@RequestParam("code") String code, HttpServletRequest request) {
        // 获取Redis，指定范围的值
        Set<Object> values = redisUtils.zSetRangeByScore(WX_MP_LOGIN_DYNAMIC_CODE, System.currentTimeMillis(), Long.MAX_VALUE);
        if (CollectionUtils.isEmpty(values)) {
            log.info("动态码已过期，请重新获取！！！动态码：{}", code);
            return ApiResult.fail(ErrorCode.NOT_FOUND_ERROR, "动态码已过期，请重新获取！！！");
        }
        // 遍历所有动态码
        boolean isValid = false;
        String openId = null;
        for (Object value : values) {
            String[] split = ((String) value).split(":");
            openId = split[0];
            String dynamicCode = split[1];
            if (dynamicCode.equals(code)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            log.error("动态码错误，请检查！动态码: {}", code);
            return ApiResult.fail(ErrorCode.NOT_FOUND_ERROR, "动态码错误！！！");
        }
        // 数据库查询
        LambdaQueryWrapper<User> qw = Wrappers.lambdaQuery(User.class).eq(User::getMpOpenId, openId);
        User user = userService.getOne(qw);
        // 判断用户是否存在
        if (ObjectUtils.isNotEmpty(user)) {
            // 记录用户的登录状态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            // 缓存用户信息
            redisUtils.set(RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getId(), user);
            // 删除Redis中的动态码
            redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, openId + ":" + code);
            // 返回用户登录信息
            return ApiResult.success(userService.getLoginUserVO(user), "登录成功");
        }
        // 自动创建用户
        // 查询管理员账号
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class).eq(User::getUserEmail, adminEmail);
        User adminUser = userService.getOne(wrapper);
        long newId = userService.addUser(new UserAddRequest(), adminUser);
        user = userService.getById(newId);
        // 设置用户唯一微信标识
        user.setMpOpenId(openId);
        ThrowUtils.throwIf(!userService.updateById(user), ErrorCode.SYSTEM_ERROR, "用户更新失败！");
        // 记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 缓存用户信息
        redisUtils.set(RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getId(), user);
        // 删除Redis中的动态码
        redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, openId + ":" + code);
        // 返回用户登录信息
        return ApiResult.success(userService.getLoginUserVO(user), "登录成功");
    }

    /**
     * 绑定微信账号
     *
     * @param code    代码
     * @param request 请求
     * @return {@link ApiResponse }<{@link UserLoginVO }>
     */
    @PostMapping("/bind")
    public ApiResponse<UserLoginVO> bind(@RequestParam String code, HttpServletRequest request) {
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        // redis 获取绑定码
        Set<Object> bindCode = redisUtils.zSetGetAllValues(WX_MP_BIND_DYNAMIC_CODE);
        ThrowUtils.throwIf(CollectionUtils.isEmpty(bindCode), ErrorCode.NOT_FOUND_ERROR, "绑定码已过期，请重新获取！！！");
        for (Object value : bindCode) {
            String[] split = ((String) value).split(":");
            if (split.length != 2) {
                log.error("动态码格式错误，请检查！动态码Value: {}", value);
                redisUtils.zSetRemoveValues(WX_MP_BIND_DYNAMIC_CODE, value);
                continue;
            }
            if (split[1].equals(code)) {
                String openId = split[0];
                if (ObjectUtils.isNotEmpty(loginUser.getMpOpenId())) {
                    log.error("用户已绑定过账号，请勿重复绑定！openId: {}", openId);
                    redisUtils.zSetRemoveValues(WX_MP_BIND_DYNAMIC_CODE, value);
                    return ApiResult.fail(ErrorCode.OPERATION_ERROR, "您已绑定过账号，请勿重复绑定！");
                }
                loginUser.setMpOpenId(openId);
                ThrowUtils.throwIf(!userService.updateById(loginUser), ErrorCode.SYSTEM_ERROR, "用户更新失败！");
                redisUtils.zSetRemoveValues(WX_MP_BIND_DYNAMIC_CODE, value);
                return ApiResult.success(userService.getLoginUserVO(loginUser), "绑定成功");
            }
        }
        log.error("绑定码错误，请检查！绑定码: {}", code);
        return ApiResult.fail(ErrorCode.NOT_FOUND_ERROR, "绑定码错误，请检查！！！");
    }

    /**
     * 取消绑定微信
     *
     * @param request 请求
     * @return {@link ApiResponse }<{@link Long }>
     */
    @PostMapping("/unbind")
    public ApiResponse<Long> unbind(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        loginUser.setMpOpenId(null);
        ThrowUtils.throwIf(!userService.updateById(loginUser), ErrorCode.SYSTEM_ERROR, "未知错误，解绑失败！");
        return ApiResult.success(loginUser.getId(), "解绑成功！");
    }

}