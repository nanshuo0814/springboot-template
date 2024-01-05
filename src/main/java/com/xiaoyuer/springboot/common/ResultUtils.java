package com.xiaoyuer.springboot.common;

/**
 * 返回工具类
 *
 * @author 小鱼儿
 * @date 2023/12/23 18:25:00
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(20000, data, "操作成功");
    }

    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> fail(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code    法典
     * @param message 消息
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> fail(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@code BaseResponse<T>}
     */
    public static <T> BaseResponse<T> fail(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
