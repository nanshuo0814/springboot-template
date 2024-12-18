package icu.nanshuo.wxmp.handler;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static icu.nanshuo.constant.WxMpConstant.WX_MP_BIND_DYNAMIC_CODE;
import static icu.nanshuo.constant.WxMpConstant.WX_MP_LOGIN_DYNAMIC_CODE;

/**
 * 公共方法
 *
 * @author @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/12/18
 */
@Slf4j
@Component
public class PublicMethods {

    @Value("${wechat.official.account.dynamicCode}")
    private String dynamicCode;
    @Value("${wechat.official.account.bindCodeExpireTime}")
    private int bindCodeExpireTime;
    @Value("${wechat.official.account.dynamicCodeExpireTime}")
    private int dynamicCodeExpireTime;
    @Value("${wechat.official.account.maxAttempts}")
    private int maxAttempts;
    @Value("${wechat.official.account.charPool}")
    private String charPool;
    @Value("${wechat.official.account.codeLength}")
    private int codeLength;

    // 随机数生成器
    private static final SecureRandom RANDOM = new SecureRandom();
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;

    /**
     * 绑定码
     *
     * @param fromUser 来自用户
     * @return {@link String }
     */
    public String generateBindCode(String fromUser) {
        // 绑定微信号登录
        // 查询用户数据库是否已绑定
        LambdaQueryWrapper<User> qw = Wrappers.lambdaQuery(User.class).eq(User::getMpOpenId, fromUser);
        User user = userService.getOne(qw);
        if (user != null) {
            log.info("微信绑定，openId：{}，user: {}", fromUser, user.getId());
            return "你已经绑定过了，无需再次绑定";
        } else {
            // 是否未过期
            Set<Object> set = redisUtils.zSetGetAllValues(WX_MP_BIND_DYNAMIC_CODE);
            if (CollectionUtils.isNotEmpty(set)) {
                for (Object value : set) {
                    // 获取动态码
                    String codeStr = (String) value;
                    // 根据 : 分解
                    String[] split = codeStr.split(":");
                    // 判断 split 是否正确（防止数组越界）
                    if (split.length != 2) {
                        log.info("绑定码格式错误，请检查！动态码：{}", value);
                        // 这里可以考虑删除该Redis
                        redisUtils.zSetRemoveValues(WX_MP_BIND_DYNAMIC_CODE, value);
                        continue; // 如果分割失败，跳过此元素
                    }
                    // 获取动态码对应的 openId
                    String bindCodeOpenId = split[0];
                    // 判断是否是当前用户
                    if (!bindCodeOpenId.equals(fromUser)) {
                        continue;
                    }
                    // 获取动态码的分数（过期时间戳）
                    Double expireTime = redisUtils.zSetGetScore(WX_MP_BIND_DYNAMIC_CODE, value);
                    // 获取当前时间戳
                    double currentTime = (double) Instant.now().toEpochMilli();
                    // 判断是否过期，通过当前时间戳和分数（过期时间戳）进行比较
                    if (ObjectUtils.isNotEmpty(expireTime) && currentTime > expireTime) {
                        // 移除Redis
                        redisUtils.zSetRemoveValues(WX_MP_BIND_DYNAMIC_CODE, value);
                        log.info("清理过期绑定码,openId: {},code: {}", bindCodeOpenId, split[1]);
                        continue;
                    }
                    // 返回未过期的动态码
                    log.info("openId: {} and 绑定码: {}", fromUser, split[1]);
                    return "绑定码：" + split[1] + "\n" +
                            "这是上一次获取的未使用过的绑定码\n请使用这个绑定码绑定";
                }
            } else {
                int count = 0;
                boolean codeExists = false;
                String code = null;
                do {
                    if (count > maxAttempts) {
                        return "绑定码生成次数过多，请动动你发财的小手，重新发送代码：“绑定”";
                    }
                    // 生成6为随机数字的动态绑定码
                    code = RandomUtil.randomNumbers(6);
                    if (CollectionUtils.isEmpty(set)) {
                        break;
                    }
                    count++;
                    for (Object value : set) {
                        String str = value.toString();  // 假设 value 是 String 类型，转换为 String
                        String[] split = str.split(":");
                        if (split.length == 2 && code.equals(split[1])) {
                            codeExists = true;
                            break;  // 找到匹配的动态码，提前退出循环
                        }
                    }
                } while (codeExists);
                // 生成时间
                LocalDateTime now = LocalDateTime.now();
                String generatedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                // 过期时间
                LocalDateTime expirationTime = now.plusMinutes(bindCodeExpireTime);
                String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Map<Object, Double> value = new HashMap<>();
                value.put(fromUser + ":" + code, (double) Instant.now().toEpochMilli() + bindCodeExpireTime * 60 * 1000d);
                redisUtils.zSetAddCustom(WX_MP_BIND_DYNAMIC_CODE, value);
                log.info("绑定码生成，openId: {} and 绑定码: {}", fromUser, code);
                return "绑定码：" + code + "\n" +
                        "请在 " + bindCodeExpireTime + " 分钟内完成绑定哦⏰\n" +
                        "生成时间：" + generatedTime + "\n" +
                        "过期时间：" + formattedExpirationTime + "\n" +
                        "若非本人操作，请忽略该消息！！！";
            }
        }
        return "发送什么事啦(O_o)??\n绑定码生成错误";
    }

    /**
     * 生成动态代码
     *
     * @param openId 打开id
     * @return {@link String }
     */
    public String generateDynamicCode(String openId) {
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
                if (split.length != 2) {
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
                        "这是上一次获取的未使用过的动态码,请使用这个动态码登录";
            }
        }
        // 生成全局唯一的6位随机验证码，由字母（区分大小写）、数字组成
        StringBuilder code;
        int count = 0;
        boolean codeExists = false;
        do {
            if (count > maxAttempts) {
                return "动态码生成次数过多，请动动你发财的小手，重新发送代码：" + dynamicCode;
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
        value.put(openId + ":" + code, (double) Instant.now().toEpochMilli() + dynamicCodeExpireTime * 60 * 1000d);
        redisUtils.zSetAddCustom(WX_MP_LOGIN_DYNAMIC_CODE, value);
        // 生成时间
        LocalDateTime now = LocalDateTime.now();
        String generatedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 过期时间
        LocalDateTime expirationTime = now.plusMinutes(dynamicCodeExpireTime);
        String formattedExpirationTime = expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // 返回动态码
        log.info("openId: {} and 动态码: {}", openId, code);
        return "动态码：" + code + "\n" +
                "请在 " + dynamicCodeExpireTime + " 分钟内完成登录哦⏰\n" +
                "生成时间：" + generatedTime + "\n" +
                "过期时间：" + formattedExpirationTime + "\n" +
                "若非本人操作，请忽略该消息！！！";
    }

}
