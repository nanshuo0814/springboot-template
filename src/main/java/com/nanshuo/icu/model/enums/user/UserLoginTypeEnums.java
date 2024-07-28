package com.nanshuo.icu.model.enums.user;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户登录类型枚举
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/28
 */
@Getter
public enum UserLoginTypeEnums {

    /**
     * 账号登录类型
     */
    account("账号登录", "account"),

    /**
     * 邮箱登录类型
     */
    email("邮箱登录", "email"),

    /**
     * 手机号登录类型
     */
    phone("手机号登录", "phone");

    private final String text;
    private final String value;


    UserLoginTypeEnums(String text, String value) {
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
    public static UserLoginTypeEnums getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserLoginTypeEnums userLoginTypeEnums : UserLoginTypeEnums.values()) {
            if (userLoginTypeEnums.value.equals(value)) {
                return userLoginTypeEnums;
            }
        }
        return null;
    }

}