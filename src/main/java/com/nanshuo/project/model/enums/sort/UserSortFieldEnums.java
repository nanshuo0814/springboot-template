package com.nanshuo.project.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.project.model.domain.User;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户排序字段
 *
 * @author nanshuo
 * @date 2024/03/31 15:31:56
 */
@Getter
public enum UserSortFieldEnums {

    ID(User::getId),
    CREATE_TIME(User::getCreateTime),
    UPDATE_TIME(User::getUpdateTime),
    USER_ACCOUNT(User::getUserAccount),
    USER_NAME(User::getUserName),
    USER_EMAIL(User::getUserEmail),
    USER_ROLE(User::getUserRole),
    USER_GENDER(User::getUserGender);

    private final SFunction<User, ?> fieldGetter;

    UserSortFieldEnums(SFunction<User, ?> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    private static final Map<String, UserSortFieldEnums> FIELD_MAPPING = Arrays.stream(values())
            .collect(Collectors.toMap(UserSortFieldEnums::name, field -> field));

    /**
     * 从字符串映射到枚举
     *
     * @param sortField 排序字段
     * @return {@code Optional<UserSortField>}
     */
    public static Optional<UserSortFieldEnums> fromString(String sortField) {
        // 转换驼峰式命名到下划线分隔，忽略大小写
        String formattedSortField = sortField.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return Optional.ofNullable(FIELD_MAPPING.get(formattedSortField.toUpperCase()));
    }

}