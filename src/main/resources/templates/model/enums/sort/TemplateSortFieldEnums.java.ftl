package ${packageName}.model.enums.sort;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import ${packageName}.model.domain.${upperDataKey};
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ${dataName}排序字段枚举
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 */
@Getter
public enum ${upperDataKey}SortFieldEnums {

    /**
     * id
     */
    ID(${upperDataKey}::getId),
    /**
     * 创建时间
     */
    CREATE_TIME(${upperDataKey}::getCreateTime),
    /**
     * 更新时间
     */
    UPDATE_TIME(${upperDataKey}::getUpdateTime);

    private final SFunction<${upperDataKey}, ?> fieldGetter;

    ${upperDataKey}SortFieldEnums(SFunction<${upperDataKey}, ?> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    private static final Map<String, ${upperDataKey}SortFieldEnums> FIELD_MAPPING = Arrays.stream(values())
            .collect(Collectors.toMap(${upperDataKey}SortFieldEnums::name, field -> field));

    /**
     * 从字符串映射到枚举
     *
     * @param sortField 排序字段
     * @return {@code Optional<${upperDataKey}SortField>}
     */
    public static Optional<${upperDataKey}SortFieldEnums> fromString(String sortField) {
        // 转换驼峰式命名到下划线分隔，忽略大小写
        String formattedSortField = sortField.replaceAll("([a-z0-9])([A-Z])", "$1_$2");
        return Optional.ofNullable(FIELD_MAPPING.get(formattedSortField.toUpperCase()));
    }
}