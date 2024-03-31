package com.nanshuo.springboot.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.springboot.model.domain.BaseEntity;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通用排序字段
 *
 * @author nanshuo
 * @date 2024/03/31 15:31:56
 */
@Getter
public enum CommonSortFieldEnums {

    ID(BaseEntity::getId),
    CREATE_TIME(BaseEntity::getCreateTime),
    UPDATE_TIME(BaseEntity::getUpdateTime);

    private final SFunction<BaseEntity, ?> fieldGetter;

    CommonSortFieldEnums(SFunction<BaseEntity, ?> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    private static final Map<String, CommonSortFieldEnums> FIELD_MAPPING = Arrays.stream(values())
            .collect(Collectors.toMap(CommonSortFieldEnums::name, field -> field));

    /**
     * 从字符串映射到枚举
     *
     * @param sortField 排序字段
     * @return {@code Optional<UserSortField>}
     */
    public static Optional<CommonSortFieldEnums> fromString(String sortField) {
        return Optional.ofNullable(FIELD_MAPPING.get(sortField));
    }

}