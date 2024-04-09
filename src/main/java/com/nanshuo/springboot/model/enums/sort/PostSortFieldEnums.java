package com.nanshuo.springboot.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.springboot.model.domain.Post;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 帖子排序字段枚举
 *
 * @author nanshuo
 * @date 2024/03/31 15:31:56
 */
@Getter
public enum PostSortFieldEnums {

    ID(Post::getId),
    CREATE_TIME(Post::getCreateTime),
    UPDATE_TIME(Post::getUpdateTime),
    FAVOUR_NUM(Post::getFavourNum),
    THUMB_NUM(Post::getThumbNum),
    USER_ID(Post::getUserId);

    private final SFunction<Post, ?> fieldGetter;

    PostSortFieldEnums(SFunction<Post, ?> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    private static final Map<String, PostSortFieldEnums> FIELD_MAPPING = Arrays.stream(values())
            .collect(Collectors.toMap(PostSortFieldEnums::name, field -> field));

    /**
     * 从字符串映射到枚举
     *
     * @param sortField 排序字段
     * @return {@code Optional<UserSortField>}
     */
    public static Optional<PostSortFieldEnums> fromString(String sortField) {
        // 转换驼峰式命名到下划线分隔，忽略大小写
        String formattedSortField = sortField.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return Optional.ofNullable(FIELD_MAPPING.get(formattedSortField.toUpperCase()));
    }
}