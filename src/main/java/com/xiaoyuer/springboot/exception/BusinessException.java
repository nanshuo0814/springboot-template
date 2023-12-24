package com.xiaoyuer.springboot.exception;


import com.xiaoyuer.springboot.common.ErrorCode;
import lombok.Getter;

/**
 * 业务异常
 * 自定义异常类
 *
 * @author 小鱼儿
 * @date 2023/12/23 18:35:31
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
