package com.xiaoyuer.springboot.utils;

import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式验证工具类
 *
 * @author 小鱼儿
 * @date 2023/12/23 21:45:27
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

    /**
     * 使用 VerifyRegexEnums 枚举中的正则表达式验证字符串
     *
     * @param regexEnums VerifyRegexEnums 枚举
     * @param value      要验证的字符串
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean matches(VerifyRegexEnums regexEnums, String value) {
        return matches(regexEnums.getRegex(), value);
    }
}