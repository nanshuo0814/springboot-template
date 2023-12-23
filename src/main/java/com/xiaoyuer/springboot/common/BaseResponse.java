package com.xiaoyuer.springboot.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:35:07
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
