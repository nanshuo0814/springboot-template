package com.nanshuo.icu.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.icu.model.domain.User;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户排序字段
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31 15:31:56
 */
@Getter
public enum UserSortFieldEnums {

    /**
     * id
     */
    ID(User::getId),
    /**
     * 创建时间
     */
    CREATE_TIME(User::getCreateTime),
    /**
     * 更新时间
     */
    UPDATE_TIME(User::getUpdateTime);

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