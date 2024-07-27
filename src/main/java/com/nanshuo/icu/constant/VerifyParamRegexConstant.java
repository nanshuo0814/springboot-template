package com.nanshuo.icu.constant;

/**
 * 验证参数正则表达式常量
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public interface VerifyParamRegexConstant {

    /**
     * 无规则
     */
    String NONE = "";

    /**
     * 用户id
     */
    String USER_ID = "^[0-9]{1,19}$";

    /**
     * 帐户
     */
    String ACCOUNT = "^[a-zA-Z][a-zA-Z0-9_-]{2,15}$";

    /**
     * 电子邮件
     */
    String EMAIL = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";

    /**
     * 用户密码
     */
    String PASSWORD = "^(?=.*[A-Za-z])(?=.*[\\d.!@#$%^&*()])(?:[A-Za-z\\d.!@#$%^&*()]){6,16}$";

    /**
     * 电话
     */
    String PHONE = "^1[3456789]\\d{9}$";

    /**
     * 图像验证码
     */
    String IMAGE_CAPTCHA = "^[a-zA-Z0-9]{4}$";

    /**
     * 电子邮件验证码
     */
    String EMAIL_CAPTCHA = "^[0-9]{6}$";

    /**
     * 用户性别
     */
    String USER_GENDER = "^[0-2]$";

    /**
     * 用户角色
     */
    String USER_ROLE = "^(user|admin)$";

    /**
     * ip
     */
    String IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

    /**
     * 用户名
     */
    String USERNAME = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
}
