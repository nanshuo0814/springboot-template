package com.nanshuo.project.exception;

import com.nanshuo.project.common.ApiResponse;
import com.nanshuo.project.common.ErrorCode;
import com.nanshuo.project.common.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理程序
 *
 * @author nanshuo
 * @date 2023/12/23 18:35:43
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理程序
     *
     * @param e e
     * @return {@code ApiResponse<?>}
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    /**
     * 运行时异常处理程序
     *
     * @param e e
     * @return {@code ApiResponse<?>}
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ApiResult.fail(ErrorCode.SYSTEM_ERROR, "系统内部错误,请联系管理员");
    }
}
