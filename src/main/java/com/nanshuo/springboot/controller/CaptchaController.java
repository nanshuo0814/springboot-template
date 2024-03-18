package com.nanshuo.springboot.controller;

import com.google.gson.Gson;
import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.common.BaseResponse;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.common.ResultUtils;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.constant.RedisKeyConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import com.nanshuo.springboot.utils.captcha.EmailCaptchaUtils;
import com.nanshuo.springboot.utils.captcha.ImageCaptchaUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码控制器
 *
 * @author 小鱼儿
 * @date 2024/01/05 15:29:46
 */
@Slf4j
@Api(tags = "验证码模块")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private final RedisTemplate<String, String> redisTemplate;

    public CaptchaController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送电子邮件验证码
     *
     * @param targetEmail 目标电子邮件
     * @return {@code BaseResponse<String>}
     */
    @PostMapping("/sendEmailCaptcha")
    @ApiOperation(value = "发送电子邮件验证码", notes = "发送电子邮件验证码")
    public BaseResponse<String> sendEmailCaptcha(
            @ApiParam(value = "目标电子邮件", required = true)
            @RequestBody @CheckParam(required = NumberConstant.TRUE_VALUE, nullErrorMsg = "邮箱不能为空",
                    regex = UserRegexEnums.EMAIL, regexErrorMsg = "邮箱格式不正确") String targetEmail) {

        // 将 JSON 数据解析为字符串（String）
        Gson gson = new Gson();
        targetEmail = gson.fromJson(targetEmail, String.class);

        // 发送邮件
        String key = RedisKeyConstant.EMAIL_CAPTCHA_KEY + targetEmail;
        // 查看redis是否有缓存验证码
        String captcha = redisTemplate.opsForValue().get(key);
        String result = "请勿重复发送验证码";
        // 如果没有缓存验证码
        if (captcha == null) {
            // 随机生成六位数验证码
            captcha = String.valueOf(new Random().nextInt(900000) + 100000);
            result = EmailCaptchaUtils.getEmailCaptcha(targetEmail, captcha);
            // 存入redis中
            captcha = gson.toJson(captcha);
            redisTemplate.opsForValue().set(key, captcha, EmailCaptchaUtils.expireTime, TimeUnit.MINUTES);
        }
        log.info("{}的邮箱验证码为：{}", targetEmail, captcha);
        // 返回结果
        return ResultUtils.success(result);
    }

    /**
     * 获取图像验证码
     *
     * @return {@code BaseResponse<String>}
     */
    @GetMapping("/getImageCaptcha")
    @ApiOperation(value = "获取图片验证码", notes = "获取图片验证码")
    public BaseResponse<String> getImageCaptcha() {
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
                return ResultUtils.fail(ErrorCode.SYSTEM_ERROR, "获取验证码失败,系统故障,请联系管理员");
            }
            log.info("图片验证码为：{}", captcha);
            // 将验证码写入 redis，过期时间为1分钟
            Gson gson = new Gson();
            String captchaJson = gson.toJson(captcha);
            redisTemplate.opsForValue().set(RedisKeyConstant.IMAGE_CAPTCHA_KEY, captchaJson, RedisKeyConstant.IMAGE_CAPTCHA_TIME_OUT, TimeUnit.SECONDS);

            // 返回 Base64 编码的验证码图片字符串
            return ResultUtils.success(base64Image);
        } catch (IOException e) {
            log.error("获取验证码失败", e);
            return ResultUtils.fail(ErrorCode.SYSTEM_ERROR, "获取验证码失败,系统故障,请联系管理员");
        }
    }

}