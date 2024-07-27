package com.nanshuo.icu.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.nanshuo.icu.model.domain.Post;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 帖子排序字段枚举
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Getter
public enum PostSortFieldEnums {

    /**
     * id
     */
    ID(Post::getId),
    /**
     * 创建时间
     */
    CREATE_TIME(Post::getCreateTime),
    /**
     * 更新时间
     */
    UPDATE_TIME(Post::getUpdateTime),
    /**
     * 收藏数
     */
    FAVOUR_NUM(Post::getFavourNum),
    /**
     * 点赞数
     */
    THUMB_NUM(Post::getThumbNum);

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