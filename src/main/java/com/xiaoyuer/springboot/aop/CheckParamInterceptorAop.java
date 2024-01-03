package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 参数检查拦截器类
 * 该类用于拦截带有 @CheckParam 注解的方法，并执行相应的参数校验逻辑。
 * 支持对参数的非空、长度、正则表达式等进行校验。
 *
 * @author 小鱼儿
 * @date 2023/12/31 00:11:45
 */
@Slf4j
@Component("CheckParamInterceptorAop")
public class CheckParamInterceptorAop {

    /**
     * 参数拦截方法
     *
     * @param joinPoint 切入点，用于获取方法和参数信息
     * @throws BusinessException 如果参数校验失败，则抛出业务异常
     */
    public void interceptor(JoinPoint joinPoint) throws BusinessException {
        try {
            // 获取方法参数
            Object[] arguments = joinPoint.getArgs();
            // 获取方法签名
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // 获取目标类的方法
            Method method = methodSignature.getMethod();
            // 遍历参数
            CommonCheckMethodAop.validateParams(false,method, arguments);
        } catch (BusinessException e) {
            log.error("拦截到业务异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw e;
        } catch (Throwable e) {
            log.error("拦截到系统异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}