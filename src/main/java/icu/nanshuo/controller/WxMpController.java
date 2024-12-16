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
import icu.nanshuo.model.dto.wxmp.WxMpTxtMsgRequest;
import icu.nanshuo.model.vo.user.UserLoginVO;
import icu.nanshuo.model.vo.wxmp.WxMpCommonMsgVO;
import icu.nanshuo.model.vo.wxmp.WxMpImgTxtItemVO;
import icu.nanshuo.model.vo.wxmp.WxMpImgTxtMsgVO;
import icu.nanshuo.model.vo.wxmp.WxMpTxtMsgVO;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static icu.nanshuo.constant.UserConstant.USER_LOGIN_STATE;
import static icu.nanshuo.constant.WxMpConstant.WX_MP_LOGIN_DYNAMIC_CODE;

/**
 * 微信公众号相关接口
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/05/13
 */
@RestController
@RequestMapping("/wx/mp")
@Slf4j
public class WxMpController {

    // region 依赖注入
    @Resource
    private WxMpService wxMpService;
    @Resource
    private WxMpMessageRouter router;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;
    // endregion
    // region 微信公众号个人信息常量
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
    @Value("${wechat.official.account.codeKeyWord}")
    private String codeKeyWord;
    @Value("${wechat.official.account.codeExpireTime}")
    private int codeExpireTime;
    @Value("${wechat.official.account.maxAttempts}")
    private int maxAttempts;
    @Value("${wechat.official.account.charPool}")
    private String charPool;
    @Value("${wechat.official.account.codeLength}")
    private int codeLength;
    @Value("${wechat.official.account.description}")
    private String description;
    @Value("${wechat.official.account.logo}")
    private String logo;
    @Value("${wechat.official.account.wxAvatar}")
    private String wxAvatar;
    @Value("${wechat.official.account.qrCode}")
    private String qrCode;
    @Value("${superAdmin.email}")
    private String adminEmail;
    // endregion
    // 随机数生成器
    private static final SecureRandom RANDOM = new SecureRandom();

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
        LambdaQueryWrapper<User> wrapper =  Wrappers.lambdaQuery(User.class).eq(User::getUserEmail, adminEmail);
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
     * 微信公众号响应消息
     *
     * @param wxMpMsgRequest wx mp msg请求
     * @return {@link WxMpCommonMsgVO }
     * @throws WxErrorException wx错误异常
     */
    @PostMapping(path = "/callback", consumes = {"application/xml", "text/xml"},
            produces = "application/xml;charset=utf-8")
    public WxMpCommonMsgVO handleWxMpMsg(@RequestBody WxMpTxtMsgRequest wxMpMsgRequest) throws WxErrorException {
        String content = wxMpMsgRequest.getContent();
        // 扫码订阅（需要服务号且要认证）
        if ("subscribe".equals(wxMpMsgRequest.getEvent()) || "scan".equalsIgnoreCase(wxMpMsgRequest.getEvent())) {
            String key = wxMpMsgRequest.getEventKey();
            if (StringUtils.isNotBlank(key) || key.startsWith("qrscene_")) {
                // 带参数的二维码，扫描、关注事件拿到之后，直接登录，省却输入验证码这一步
                // 带参数二维码需要 微信认证，个人公众号无权限
                String code = key.substring("qrscene_".length());
                // todo 实现自动注册用户账号
                // 登录成功后，发送一条公众号消息给用户，提示登录成功
                WxMpTxtMsgVO res = new WxMpTxtMsgVO();
                res.setContent("登录成功");
                fillResVo(res, wxMpMsgRequest);
                return res;
            }
        }
        // 构造回复消息
        WxMpCommonMsgVO res = buildResponseBody(wxMpMsgRequest.getEvent(), content, wxMpMsgRequest.getFromUserName());
        fillResVo(res, wxMpMsgRequest);
        return res;
    }

    /**
     * 构建响应正文
     *
     * @param eventType 事件类型
     * @param content   内容
     * @param fromUser  来自用户
     * @return {@link WxMpCommonMsgVO }
     */
    public WxMpCommonMsgVO buildResponseBody(String eventType, String content, String fromUser) {
        // 返回的文本消息
        String textRes = null;
        // 返回的是图文消息
        List<WxMpImgTxtItemVO> imgTxtList = null;
        // 关注订阅微信公众号
        if ("subscribe".equalsIgnoreCase(eventType)) {
            textRes = "\uD83C\uDF89 叮咚～你终于来啦！\n" +
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
                    "\uD83C\uDF1F备注：\n网站微信登录的动态码查看，请回复“" + codeKeyWord + "”关键词获取";
            log.info("用户关注公众号，openId = {}", fromUser);
        }
        // 下面是关键词回复
        // 关键词获取登录动态码
        else if (codeKeyWord.equalsIgnoreCase(content)) {
            // 动态码获取
            textRes = generateDynamicCode(fromUser);
        }
        // 回复图文消息
        else if ("微信".equalsIgnoreCase(content)) {
            WxMpImgTxtItemVO imgTxt = new WxMpImgTxtItemVO();
            imgTxt.setTitle("站长微信二维码");
            imgTxt.setDescription("扫码加站长微信！！！");
            imgTxt.setPicUrl(wxAvatar);
            imgTxt.setUrl(qrCode);
            imgTxtList = Collections.singletonList(imgTxt);
            log.info("openId: {},获取微信二维码", fromUser);
        }
        // 回复图文消息
        else if ("网站".equalsIgnoreCase(content)) {
            WxMpImgTxtItemVO imgTxt = new WxMpImgTxtItemVO();
            imgTxt.setTitle("站长个人网站（" + wxMpName + "）");
            imgTxt.setDescription(description);
            imgTxt.setPicUrl(logo);
            imgTxt.setUrl(website);
            imgTxtList = Collections.singletonList(imgTxt);
            log.info("openId: {},获取网站链接", fromUser);
        }
        // 默认回复
        else {
            textRes = "/:? 你好像迷路了鸭？\n" +
                    "可以试着回复以下关键词：\n" +
                    "- 微信\n" +
                    "- 网站";
            log.info("openId = {}，触发默认回复", fromUser);
        }
        if (textRes != null) {
            WxMpTxtMsgVO vo = new WxMpTxtMsgVO();
            vo.setContent(textRes);
            return vo;
        } else {
            WxMpImgTxtMsgVO vo = new WxMpImgTxtMsgVO();
            vo.setArticles(imgTxtList);
            vo.setArticleCount(imgTxtList.size());
            return vo;
        }
    }

    /**
     * 填充响应消息
     *
     * @param res            res
     * @param wxMpMsgRequest wx mp msg请求
     */
    private void fillResVo(WxMpCommonMsgVO res, WxMpTxtMsgRequest wxMpMsgRequest) {
        res.setFromUserName(wxMpMsgRequest.getToUserName());
        res.setToUserName(wxMpMsgRequest.getFromUserName());
        res.setCreateTime(System.currentTimeMillis() / 1000);
    }

    /**
     * 生成动态代码
     *
     * @param openId 打开id
     * @return {@link String }
     */
    private String generateDynamicCode(String openId) {
        // 获取到所有的 value
        Set<Object> set = redisUtils.zSetGetAllValues(WX_MP_LOGIN_DYNAMIC_CODE);
        // 遍历循环获取每个 value（结构：{openId}:{动态码}） 和 score（过期时间戳）
        if (CollectionUtils.isNotEmpty(set)) {
            for (Object value : set) {
                // 获取动态码
                String codeStr = (String) value;
                // 根据 : 分解
                String[] split = codeStr.split(":");
                // 判断 split 是否正确（防止数组越界）
                if (split.length < 2) {
                    log.info("动态码格式错误，请检查！动态码：{}", value);
                    // 这里可以考虑删除该Redis
                    redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, value);
                    continue; // 如果分割失败，跳过此元素
                }
                // 获取动态码对应的 openId
                String dynamicCodeOpenId = split[0];
                // 判断是否是当前用户
                if (!dynamicCodeOpenId.equals(openId)) {
                    continue;
                }
                // 获取动态码的分数（过期时间戳）
                Double expireTime = redisUtils.zSetGetScore(WX_MP_LOGIN_DYNAMIC_CODE, value);
                // 获取当前时间戳
                double currentTime = (double) Instant.now().toEpochMilli();
                // 判断是否过期，通过当前时间戳和分数（过期时间戳）进行比较
                if (ObjectUtils.isNotEmpty(expireTime) && currentTime > expireTime) {
                    // 移除Redis
                    redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, value);
                    continue;
                }
                // 返回未过期的动态码
                log.info("openId: {} and 动态码: {}", openId, split[1]);
                return "动态码：" + split[1] + "\n" +
                        "这是上一次获取的未使用过的动态码\n请使用这个动态码登录";
            }
        }
        // 生成全局唯一的6位随机验证码，由字母（区分大小写）、数字组成
        StringBuilder code;
        int count = 0;
        boolean codeExists = false;
        do {
            if (count > maxAttempts) {
                return "动态码生成次数过多，请动动你发财的小手，重新发送代码：" + codeKeyWord;
            }
            code = new StringBuilder();
            for (int i = 0; i < codeLength; i++) {
                int index = RANDOM.nextInt(charPool.length());
                code.append(charPool.charAt(index));
            }
            if (CollectionUtils.isEmpty(set)) {
                break;
            }
            count++;
            for (Object value : set) {
                String str = value.toString();  // 假设 value 是 String 类型，转换为 String
                String[] split = str.split(":");
                if (split.length == 2 && code.toString().equals(split[1])) {
                    codeExists = true;
                    break;  // 找到匹配的动态码，提前退出循环
                }
            }
        } while (codeExists);
        // 储存到Redis里
        Map<Object, Double> value = new HashMap<>();
        value.put(openId + ":" + code, (double) Instant.now().toEpochMilli() + codeExpireTime * 60 * 1000d);
        redisUtils.zSetAddCustom(WX_MP_LOGIN_DYNAMIC_CODE, value);
        // 生成时间
        LocalDateTime now = LocalDateTime.now();
        String generatedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 过期时间
        LocalDateTime expirationTime = now.plusMinutes(codeExpireTime);
        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 返回动态码
        log.info("openId: {} and 动态码: {}", openId, code);
        return "动态码：" + code + "\n" +
                "请在 " + codeExpireTime + " 分钟内完成登录哦⏰\n" +
                "生成时间：" + generatedTime + "\n" +
                "过期时间：" + formattedExpirationTime + "\n" +
                "若非本人操作，请忽略该消息！！！";
    }

    /**
     * 设置公众号菜单（订阅号没有这个权限，至少要有服务号）
     * 目前个人可以申请服务号了
     *
     * @return {@code String}
     * @throws WxErrorException wx错误异常
     */
    @GetMapping("/setMenu")
    public String setMenu() throws WxErrorException {
        log.info("setMenu");
        WxMenu wxMenu = new WxMenu();
        // 菜单一
        WxMenuButton wxMenuButton1 = new WxMenuButton();
        wxMenuButton1.setType(MenuButtonType.VIEW);
        wxMenuButton1.setName("主菜单一");
        // 子菜单
        WxMenuButton wxMenuButton1SubButton1 = new WxMenuButton();
        wxMenuButton1SubButton1.setType(MenuButtonType.VIEW);
        wxMenuButton1SubButton1.setName("跳转页面");
        wxMenuButton1SubButton1.setUrl(
                "https://nanshuo.icu");
        wxMenuButton1.setSubButtons(Collections.singletonList(wxMenuButton1SubButton1));

        // 菜单二
        WxMenuButton wxMenuButton2 = new WxMenuButton();
        wxMenuButton2.setType(MenuButtonType.CLICK);
        wxMenuButton2.setName("点击事件");
        wxMenuButton2.setKey("CLICK_MENU_KEY");

        // 菜单三
        WxMenuButton wxMenuButton3 = new WxMenuButton();
        wxMenuButton3.setType(MenuButtonType.VIEW);
        wxMenuButton3.setName("主菜单三");
        WxMenuButton wxMenuButton3SubButton1 = new WxMenuButton();
        wxMenuButton3SubButton1.setType(MenuButtonType.VIEW);
        wxMenuButton3SubButton1.setName("烁烁南光");
        wxMenuButton3SubButton1.setUrl("https://nanshuo.icu");
        wxMenuButton3.setSubButtons(Collections.singletonList(wxMenuButton3SubButton1));

        // 设置主菜单
        wxMenu.setButtons(Arrays.asList(wxMenuButton1, wxMenuButton2, wxMenuButton3));
        wxMpService.getMenuService().menuCreate(wxMenu);
        return "ok";
    }

}
