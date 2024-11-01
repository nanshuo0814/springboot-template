package icu.nanshuo.controller;

import icu.nanshuo.annotation.CheckParam;
import icu.nanshuo.annotation.Verify;
import icu.nanshuo.common.ApiResponse;
import icu.nanshuo.common.ApiResult;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.constant.RedisKeyConstant;
import icu.nanshuo.constant.UserConstant;
import icu.nanshuo.constant.VerifyParamRegexConstant;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.model.enums.user.UserEmailCaptchaTypeEnums;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.JsonUtils;
import icu.nanshuo.utils.captcha.EmailCaptchaUtils;
import icu.nanshuo.utils.captcha.ImageCaptchaUtils;
import icu.nanshuo.utils.redis.RedisUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码接口
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/05
 */
@Slf4j
//@Api(tags = "验证码模块")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;

    /**
     * 发送电子邮件验证码
     *
     * @param email 目标电子邮件
     * @return {@code ApiResponse<String>}
     */
    @PostMapping("/sendEmailCaptcha")
    @ApiOperation(value = "发送邮件验证码")
    @Verify(checkParam = true)
    public ApiResponse<String> sendEmailCaptcha(
            @RequestParam @ApiParam(value = "接受验证码的邮箱", required = true)
            @CheckParam(alias = "邮箱", regex = VerifyParamRegexConstant.EMAIL) String email,
            @RequestParam @ApiParam(value = "邮箱验证码类型", required = true)
            @CheckParam(alias = "邮箱验证码类型") String type) {
        // 获取邮箱类型枚举
        UserEmailCaptchaTypeEnums emailCaptchaTypeEnum = UserEmailCaptchaTypeEnums.getEnumByValue(type);
        if (emailCaptchaTypeEnum == null) {
            log.error("邮箱类型错误:{}", type);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱类型错误");
        }
        // 判断一下如果是重置密码的邮箱，则判断是否是注册邮箱
        if (emailCaptchaTypeEnum.getValue().equals(UserEmailCaptchaTypeEnums.reset.getValue())) {
            boolean flag = userService.validateEmail(email);
            if (!flag) {
                log.error("该邮箱未注册:{}", email);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该邮箱未注册");
            }
        }
        String key = RedisKeyConstant.EMAIL_CAPTCHA_KEY + emailCaptchaTypeEnum.getValue() + ":" + email;
        // 查看redis是否有缓存验证码
        String captcha = (String) redisUtils.get(key);
        String result = "请勿重复发送验证码，请使用之前发送的验证码";
        // 如果没有缓存验证码
        if (captcha == null) {
            // 随机生成六位数验证码
            captcha = String.valueOf(new Random().nextInt(900000) + 100000);
            // 发送邮件
            result = EmailCaptchaUtils.getEmailCaptcha(email, captcha);
            // 存入redis中
            captcha = JsonUtils.objToJson(captcha);
            redisUtils.set(key, captcha, EmailCaptchaUtils.expireTime, TimeUnit.MINUTES);
        }
        log.info("{}的邮箱验证码为：{}", email, captcha);
        // 返回结果
        return ApiResult.success(null, result);
    }

    /**
     * 检查电子邮件验证码
     *
     * @param email   电子邮件
     * @param type    类型
     * @param captcha 验证码
     * @return {@link ApiResponse }<{@link String }>
     */
    @PostMapping("/checkEmailCaptcha")
    @ApiOperation(value = "验证邮箱验证码")
    @Verify(checkParam = true)
    public ApiResponse<String> checkEmailCaptcha(
            @RequestParam @ApiParam(value = "要验证验证码的邮箱", required = true)
            @CheckParam(alias = "要验证验证码的邮箱", regex = VerifyParamRegexConstant.EMAIL) String email,
            @RequestParam @ApiParam(value = "邮箱验证码类型", required = true) @CheckParam(alias = "邮箱验证码类型") String type,
            @RequestParam @ApiParam(value = "验证码", required = true) @CheckParam(alias = "验证码") String captcha) {
        boolean flag = userService.validateEmailCode(email, captcha, type);
        if (!flag) {
            log.error("验证码错误:{}", captcha);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
        }
        // 生成唯一值（凭证）
        String captchaKey = RedisKeyConstant.EMAIL_CAPTCHA_KEY + type;
        String resetKey = captchaKey + "_" + RedisKeyConstant.VOUCHER;
        String value = String.valueOf(redisUtils.globalUniqueKey(captchaKey));
        // 缓存5分钟
        redisUtils.set(resetKey, value, 3, TimeUnit.MINUTES);
        return ApiResult.success(value, "邮箱验证码验证成功！");
    }

    /**
     * 获取图像验证码
     *
     * @return {@code ApiResponse<String>}
     */
    @GetMapping("/getImageCaptcha")
    @ApiOperation(value = "获取图片验证码")
    public ApiResponse<Map<String, String>> getImageCaptcha() {
        try {
            ImageCaptchaUtils imageCaptchaUtils = new ImageCaptchaUtils();

            // 使用 ByteArrayOutputStream 将图片写入字节数组输出流
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageCaptchaUtils.generateCaptcha(byteArrayOutputStream);

            // 将字节数组输出流的内容转换为字节数组
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // 使用 Base64 类将字节数组编码为 Base64 字符串
            String image = Base64.getEncoder().encodeToString(imageBytes);
            String base64Image = "data:image/png;base64," + image;

            // 获取验证码
            String captcha = imageCaptchaUtils.getCaptcha();
            if (captcha == null) {
                return ApiResult.fail(ErrorCode.SYSTEM_ERROR, "获取验证码失败,系统故障,请联系管理员");
            }
            log.info("图片验证码为：{}", captcha);

            // 将验证码写入 redis，过期时间为1分钟
            String captchaJson = JsonUtils.objToJson(captcha);
            String globalUniqueKey = String.valueOf(redisUtils.globalUniqueKey("imageCaptcha"));
            String captchaKey = RedisKeyConstant.IMAGE_CAPTCHA_KEY + globalUniqueKey;

            // 将验证码存入redis,过期时间为1分钟
            redisUtils.set(captchaKey, captchaJson, UserConstant.IMAGE_CAPTCHA_TIME_OUT, TimeUnit.SECONDS);

            Map<String, String> map = new HashMap<>();
            map.put("captcha", base64Image);
            map.put("captchaKey", globalUniqueKey);

            // 返回 Base64 编码的验证码图片字符串 + 验证码Key
            return ApiResult.success(map);
        } catch (IOException e) {
            log.error("获取验证码失败", e);
            return ApiResult.fail(ErrorCode.SYSTEM_ERROR, "获取验证码失败,系统故障,请联系管理员");
        }
    }

}