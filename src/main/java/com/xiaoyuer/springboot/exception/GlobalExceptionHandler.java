package com.xiaoyuer.springboot.exception;

import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理程序
 *
 * @author 小鱼儿
 * @date 2023/12/23 18:35:43
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理程序
     *
     * @param e e
     * @return {@code BaseResponse<?>}
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常处理程序
     *
     * @param e e
     * @return {@code BaseResponse<?>}
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统内部错误,请联系管理员");
    }
}
