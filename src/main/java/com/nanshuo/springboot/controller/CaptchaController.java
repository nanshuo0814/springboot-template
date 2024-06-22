package com.nanshuo.springboot.controller;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.common.ApiResponse;
import com.nanshuo.springboot.common.ApiResult;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.constant.RedisKeyConstant;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import com.nanshuo.springboot.utils.JsonUtils;
import com.nanshuo.springboot.utils.captcha.EmailCaptchaUtils;
import com.nanshuo.springboot.utils.captcha.ImageCaptchaUtils;
import com.nanshuo.springboot.utils.redis.RedisUtils;
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
 * 验证码控制器
 *
 * @author nanshuo
 * @date 2024/01/05 15:29:46
 */
@Slf4j
//@Api(tags = "验证码模块")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private RedisUtils redisUtils;

    /**
     * 发送电子邮件验证码
     *
     * @param targetEmail 目标电子邮件
     * @return {@code ApiResponse<String>}
     */
    @PostMapping("/sendEmailCaptcha")
    @ApiOperation(value = "发送电子邮件验证码")
    public ApiResponse<String> sendEmailCaptcha(
            @ApiParam(value = "目标电子邮件", required = true)
            @RequestBody @CheckParam(required = NumberConstant.TRUE_ONE_VALUE, alias = "邮箱",
                    regex = UserRegexEnums.EMAIL) String targetEmail) {

        // 将 JSON 数据解析为字符串（String）
        targetEmail = JsonUtils.jsonToObj(targetEmail, String.class);

        String key = RedisKeyConstant.EMAIL_CAPTCHA_KEY + targetEmail;
        // 查看redis是否有缓存验证码
        String captcha = (String) redisUtils.get(key);
        String result = "请勿重复发送验证码";
        // 如果没有缓存验证码
        if (captcha == null) {
            // 随机生成六位数验证码
            captcha = String.valueOf(new Random().nextInt(900000) + 100000);
            // 发送邮件
            result = EmailCaptchaUtils.getEmailCaptcha(targetEmail, captcha);
            // 存入redis中
            captcha = JsonUtils.objToJson(captcha);
            redisUtils.set(key, captcha, EmailCaptchaUtils.expireTime, TimeUnit.MINUTES);
        }
        log.info("{}的邮箱验证码为：{}", targetEmail, captcha);
        // 返回结果
        return ApiResult.success(result);
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
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

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

            // 将验证码存入redis,过期时间为2分钟
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