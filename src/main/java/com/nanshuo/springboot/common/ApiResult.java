package com.nanshuo.springboot.common;

/**
 * 返回Api结果类
 *
 * @author nanshuo
 * @date 2023/12/23 18:25:00
 */
public class ApiResult {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@code BaseResponse<T>}
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(20000, data, "操作成功");
    }

    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @return {@code BaseResponse<T>}
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code    法典
     * @param message 消息
     * @return {@code BaseResponse<T>}
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@code BaseResponse<T>}
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), null, message);
    }
}
