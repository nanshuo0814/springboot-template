package com.nanshuo.springboot.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author nanshuo
 * @date 2023/12/23 16:35:07
 */
@Data
public class ApiResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public ApiResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ApiResponse(int code, T data) {
        this(code, data, "");
    }

    public ApiResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
