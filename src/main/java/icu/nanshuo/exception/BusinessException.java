package icu.nanshuo.exception;


import icu.nanshuo.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
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

    /**
     * 业务异常（自定义错误码）
     *
     * @param errorCode 错误代码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 业务异常(自定义错误码和错误信息）
     *
     * @param errorCode 错误代码
     * @param message   信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 业务异常（参数异常40000）
     *
     * @param message 信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.PARAMS_ERROR.getCode();
    }

}
