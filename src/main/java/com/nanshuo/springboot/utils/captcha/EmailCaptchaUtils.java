package com.nanshuo.springboot.utils.captcha;

import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 邮箱验证码工具类
 *
 * @author nanshuo
 * @date 2024/01/05 17:07:19
 */
@Slf4j
@Component
public class EmailCaptchaUtils {

    /**
     * 主机名
     */
    private static String hostName;

    /**
     * 电子邮件地址
     */
    private static String emailAddress;

    /**
     * 密码
     */
    private static String password;

    /**
     * 发件人姓名
     */
    private static String senderName;

    /**
     * ssl smtp端口
     */
    private static int sslSmtpPort;

    /**
     * 内容
     */
    private static String content;

    /**
     * 邮件标题
     */
    private static String mailTitle;

    /**
     * 过期时间
     */
    public static Integer expireTime;

    @Value("${email.hostName}")
    public void setHostName(String hostName) {
        EmailCaptchaUtils.hostName = hostName;
    }

    @Value("${email.emailAddress}")
    public void setEmailAddress(String emailAddress) {
        EmailCaptchaUtils.emailAddress = emailAddress;
    }

    @Value("${email.password}")
    public void setPassword(String password) {
        EmailCaptchaUtils.password = password;
    }

    @Value("${email.senderName}")
    public void setSenderName(String senderName) {
        EmailCaptchaUtils.senderName = senderName;
    }

    @Value("${email.sslSmtpPort}")
    public void setSslSmtpPort(int sslSmtpPort) {
        EmailCaptchaUtils.sslSmtpPort = sslSmtpPort;
    }

    @Value("${email.content}")
    public void setContent(String content) {
        EmailCaptchaUtils.content = content;
    }

    @Value("${email.mailTitle}")
    public void setMailTitle(String mailTitle) {
        EmailCaptchaUtils.mailTitle = mailTitle;
    }

    @Value("${email.expireTime}")
    public void setExpireTime(Integer expireTime) {
        EmailCaptchaUtils.expireTime = expireTime;
    }

    /**
     * 获取电子邮件验证码
     *
     * @param targetEmail 目标用户邮箱
     * @param captcha     发送的验证码
     * @return {@code String}
     */
    public static String getEmailCaptcha(String targetEmail, String captcha) {
        try {
            // 创建邮箱对象
            SimpleEmail mail = new SimpleEmail();
            // 设置发送邮件的服务器
            mail.setHostName(hostName);
            // "你的邮箱号"+ "上文开启SMTP(16位)获得的授权码"
            mail.setAuthentication(emailAddress, password);
            // 发送邮件 "你的邮箱号"+"发送时用的标题"
            mail.setFrom(emailAddress, senderName);
            // 发送服务端口
            mail.setSslSmtpPort(String.valueOf(sslSmtpPort));
            // 使用安全链接
            mail.setSSLOnConnect(true);
            System.setProperty("mail.smtp.ssl.enable", "true");
            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
            // 接收用户的邮箱
            mail.addTo(targetEmail);
            // 邮件的主题(标题)
            mail.setSubject(mailTitle);
            // 邮件的内容
            mail.setMsg("【" + content + "】您的邮箱验证码为: " + captcha + " (" + expireTime + "分钟内有效)，如非本人操作，请忽略此邮件。");
            // 发送
            mail.send();
            return "邮箱验证码发送成功，请注意查收！";
        } catch (EmailException e) {
            log.error("发送邮件失败！");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，发送邮件失败！");
        }
    }
}
