package com.nanshuo.project.model.enums.user;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户电子邮件captcha类型枚举
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/06/24
 */
@Getter
public enum UserEmailCaptchaTypeEnums {

    register("邮箱注册验证码", "register"),
    reset("邮箱重置密码验证码", "reset"),
    others("邮箱其他验证码", "others");

    private final String text;
    private final String value;


    UserEmailCaptchaTypeEnums(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return {@code List<String>}
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 按值获取枚举
     * 根据 value 获取枚举
     *
     * @param value 价值
     * @return {@code UserRoleEnum}
     */
    public static UserEmailCaptchaTypeEnums getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserEmailCaptchaTypeEnums userEmailCaptchaTypeEnums : UserEmailCaptchaTypeEnums.values()) {
            if (userEmailCaptchaTypeEnums.value.equals(value)) {
                return userEmailCaptchaTypeEnums;
            }
        }
        return null;
    }

}
