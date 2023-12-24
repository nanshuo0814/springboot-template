package com.xiaoyuer.springboot.model.enums;

import lombok.Getter;

/**
 * 验证正则表达式枚举
 *
 * @author 小鱼儿
 * @date 2023/12/23 20:34:50
 */
@Getter
public enum VerifyRegexEnums {

    NO("", "不校验"),
    ACCOUNT("^[a-zA-Z][a-zA-Z0-9_]{2,10}$", "字母开头,由数字、英文字母或者下划线组成 3-11位"),
    EMAIL("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$", "邮箱"),
    PASSWORD("^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{6,18}$", "只能是数字，字母，特殊字符 6-18位"),
    PHONE("^1[3456789]\\d{9}$", "手机号"),
    CHECK_CODE("^[a-zA-Z0-9]{4}$", "验证码 4位,由字母数字组成"),
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

    VerifyRegexEnums(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

}