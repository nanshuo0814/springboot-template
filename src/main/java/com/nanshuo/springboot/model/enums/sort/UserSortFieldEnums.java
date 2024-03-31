package com.nanshuo.springboot.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.springboot.model.domain.User;
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
        return Optional.ofNullable(FIELD_MAPPING.get(sortField));
    }
}