package com.nanshuo.icu.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证工具类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public class RegexUtils {

    /**
     * 验证给定字符串是否匹配正则表达式
     *
     * @param regex 正则表达式
     * @param value 要验证的字符串
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean matches(String regex, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

}