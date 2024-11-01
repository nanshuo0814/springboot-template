package icu.nanshuo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * api响应
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/27
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = -5456284309934779361L;

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
