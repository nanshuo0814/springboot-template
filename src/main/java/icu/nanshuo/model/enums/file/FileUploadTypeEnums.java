package icu.nanshuo.model.enums.file;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/26 14:23:52
 */
@Getter
public enum FileUploadTypeEnums {

    // todo 业务类型枚举
    USER_AVATAR("用户头像", "user_avatar"),
    APPLICATION_COVER("应用封面", "application_cover");

    private final String text;

    private final String value;

    FileUploadTypeEnums(String text, String value) {
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
     * @param value 值
     * @return {@code FileUploadTypeEnums}
     */
    public static FileUploadTypeEnums getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadTypeEnums anEnum : FileUploadTypeEnums.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
