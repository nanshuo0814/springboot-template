package com.nanshuo.springboot.aop;

import com.nanshuo.springboot.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义 AOP 检查参数配置类
 *
 * @author nanshuo
 * @date 2024/03/22 21:52:06
 */
@Slf4j
@Aspect
@Configuration("CustomInterceptorAop")
@RequiredArgsConstructor
public class CustomInterceptorAop {

    private final CustomCheckAopMethod customCheckAopMethod;

    /**
     * 切入带有 @Check 注解的方法。
     */
    @Pointcut("@annotation(com.nanshuo.springboot.annotation.Check)")
    public void checkPointcut() {
    }

    /**
     * 在带有 @Check 注解的方法执行前，执行 CheckInterceptorAop 的拦截逻辑。
     *
     * @param joinPoint 切入点对象，包含方法的相关信息。
     * @throws BusinessException 拦截到业务异常时抛出 BusinessException。
     */
    @Before("checkPointcut()")
    public void doCheckInterceptor(JoinPoint joinPoint) throws BusinessException {
        customCheckAopMethod.interceptor(joinPoint);
    }

}