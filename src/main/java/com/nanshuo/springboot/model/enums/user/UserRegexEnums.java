package com.nanshuo.springboot.model.enums.user;

import lombok.Getter;

/**
 * 验证正则表达式枚举
 *
 * @author 小鱼儿
 * @date 2023/12/23 20:34:50
 */
@Getter
public enum UserRegexEnums {

    NO("", "不校验"),
    USER_ID("^[0-9]{1,19}$", "用户id"),
    ACCOUNT("^[a-zA-Z][a-zA-Z0-9_]{2,15}$", "字母开头,由数字、英文字母或者下划线组成 3-16位"),
    EMAIL("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$", "邮箱"),
    PASSWORD("^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{6,18}$", "至少包含一个数字，一个字母，可以有特殊字符，长度为6-18位"),
    PHONE("^1[3456789]\\d{9}$", "手机号"),
    IMAGE_CAPTCHA("^[a-zA-Z0-9]{4}$", "验证码 4位,由字母数字组成"),
    EMAIL_CAPTCHA("^[0-9]{6}$", "邮箱验证码 6位,由数字组成"),
    USER_GENDER("^[0-2]$", "性别，0:女, 1:男, 2:未知"),
    USER_ROLE("^(user|admin)$", "用户角色，只能是 user 或 admin"),
    IP("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}", "IP地址"),
    USERNAME("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", "用户名，可以包含字母、数字和汉字");


    /**
     * 正则表达式
     */
    private final String regex;

    /**
     * 描述
     */
    private final String desc;

    UserRegexEnums(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

}