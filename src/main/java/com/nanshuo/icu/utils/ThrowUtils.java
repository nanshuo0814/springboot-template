package com.nanshuo.icu.utils;


import com.nanshuo.icu.common.ErrorCode;
import com.nanshuo.icu.exception.BusinessException;

/**
 * 抛异常工具类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition        条件
     * @param runtimeException 运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误代码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误代码
     * @param message   消息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }


    /**
     * 如果为 null，则抛出
     *
     * @param data 数据
     */
    public static void throwIfNull(Object data) {
        throwIf(data == null, ErrorCode.PARAMS_NULL);
    }
}
