package com.nanshuo.icu.common;

/**
 * 返回Api结果类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23
 */
public class ApiResult {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@code ApiResponse<T>}
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), data, "操作成功");
    }

    /**
     * 成功
     *
     * @param data    数据
     * @param message 信息
     * @return {@link ApiResponse }<{@link T }>
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), data, message);
    }

    /**
     * 成功
     *
     * @param code    代码
     * @param message 信息
     * @return {@link ApiResponse }<{@link T }>
     */
    public static <T> ApiResponse<T> success(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }


    /**
     * 成功
     *
     * @param code 代码
     * @param data 数据
     * @return {@link ApiResponse }<{@link T }>
     */
    public static <T> ApiResponse<T> success(int code, T data) {
        return new ApiResponse<>(code, data, "操作成功");
    }

    /**
     * 成功
     *
     * @param code    代码
     * @param data    数据
     * @param message 信息
     * @return {@link ApiResponse }<{@link T }>
     */
    public static <T> ApiResponse<T> success(int code, T data, String message) {
        return new ApiResponse<>(code, data, message);
    }


    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @return {@code ApiResponse<T>}
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code    法典
     * @param message 消息
     * @return {@code ApiResponse<T>}
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@code ApiResponse<T>}
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), null, message);
    }
}
