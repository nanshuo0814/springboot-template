package icu.nanshuo.job.cycle;

import icu.nanshuo.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Set;

import static icu.nanshuo.constant.WxMpConstant.WX_MP_LOGIN_DYNAMIC_CODE;

/**
 * 微信公众号登录动态代码定期清洁服务
 *
 * @author 南烁
 * @date 2024/12/15
 */
@Slf4j
@Service
public class WxMpDynamicCodeRegularCleanService {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 每隔 1 分钟清理过期的动态码
     * 清除过期动态代码
     */
    //@Scheduled(fixedRate = 60000)  // 用来测试，60000 毫秒 = 1 分钟
    @Scheduled(cron = "0 30 2 * * ?")  // 每天凌晨 2 点 30 分执行
    public void run() {
        // 获取 Redis 中存储的所有动态码
        Set<Object> codeSet = redisUtils.zSetGetAllValues(WX_MP_LOGIN_DYNAMIC_CODE);
        if (ObjectUtils.isEmpty(codeSet)) {
            log.info("没有动态码，无需清理");
            return;  // 如果没有动态码，直接返回
        }
        // 当前时间戳
        double currentTime = (double) Instant.now().toEpochMilli();
        // 遍历所有动态码，检查是否过期
        for (Object value : codeSet) {
            String codeStr = (String) value;
            String[] split = codeStr.split(":");
            // 如果动态码格式不正确，跳过
            if (split.length != 2) {
                log.error("动态码格式错误，请检查！动态码Value: {}", value);
                // 考虑删除该Redis
                redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, value);
                continue;
            }
            String dynamicCode = split[1];  // 获取动态码
            Double expireTime = redisUtils.zSetGetScore(WX_MP_LOGIN_DYNAMIC_CODE, value);  // 获取过期时间
            if (ObjectUtils.isNotEmpty(expireTime) && currentTime > expireTime) {
                // 如果过期，删除该动态码
                redisUtils.zSetRemoveValues(WX_MP_LOGIN_DYNAMIC_CODE, value);
                log.info("清理过期动态码: {}", dynamicCode);  // 打印日志或其他操作
            } else {
                log.info("用户: {},动态码未过期，跳过清理: {}", split[0], dynamicCode);
            }
        }
    }

}
