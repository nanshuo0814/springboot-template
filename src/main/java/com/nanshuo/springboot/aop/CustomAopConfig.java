package com.nanshuo.springboot.aop;

import com.nanshuo.springboot.annotation.Check;
import com.nanshuo.springboot.annotation.CheckAuth;
import com.nanshuo.springboot.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 自定义 AOP 配置类，用于配置切面拦截器。
 * 包含三个切入点：
 * 1. checkPointcut: 切入带有 @Check 注解的方法。
 * 2. checkAuthPointcut: 切入带有 @CheckAuth 注解的方法。
 * 3. paramCheckPointcut: 切入 controller 包下的所有方法，但排除带有 @Check 注解的方法。
 * 通过 @Order 注解指定拦截器的执行顺序。
 * 在切入点前执行相应的拦截逻辑。
 *
 * @author 小鱼儿
 * @date 2023/12/30 20:25:07
 */
@Slf4j
@Aspect
@Configuration("CustomAopConfig")
public class CustomAopConfig {

    private final CheckAuthInterceptorAop authInterceptor;

    private final CheckInterceptorAop checkInterceptorAop;

    private final CheckParamInterceptorAop checkParamInterceptorAop;

    public CustomAopConfig(CheckAuthInterceptorAop authInterceptor, CheckInterceptorAop checkInterceptorAop, CheckParamInterceptorAop checkParamInterceptorAop) {
        this.authInterceptor = authInterceptor;
        this.checkInterceptorAop = checkInterceptorAop;
        this.checkParamInterceptorAop = checkParamInterceptorAop;
    }

    /**
     * 切入带有 @Check 注解的方法。
     */
    @Pointcut("@annotation(com.nanshuo.springboot.annotation.Check)")
    public void checkPointcut() {
    }

    /**
     * 切入带有 @CheckAuth 注解的方法。
     */
    @Pointcut("@annotation(com.nanshuo.springboot.annotation.CheckAuth)")
    public void checkAuthPointcut() {
    }

    /**
     * 切入 controller 包下的所有方法，但排除带有 @Check 注解的方法。
     */
    @Pointcut("execution(* com.nanshuo.springboot.controller.*.*(..)) && !@annotation(com.nanshuo.springboot.annotation.Check)")
    public void paramCheckPointcut() {
    }

    /**
     * 在带有 @CheckAuth 注解的方法执行前，执行 CheckAuthInterceptorAop 的拦截逻辑。
     *
     * @param checkAuth 方法上的 @CheckAuth 注解对象。
     */
    @Order(1)
    @Before("@annotation(checkAuth)")
    public void doCheckAuthInterceptor(CheckAuth checkAuth) {
        authInterceptor.doInterceptor(checkAuth);
    }

    /**
     * 在带有 @Check 注解的方法执行前，执行 CheckInterceptorAop 的拦截逻辑。
     *
     * @param joinPoint 切入点对象，包含方法的相关信息。
     * @throws BusinessException 拦截到业务异常时抛出 BusinessException。
     */
    @Order(2)
    @Before("checkPointcut()")
    public void doCheckInterceptor(JoinPoint joinPoint) throws BusinessException {
        checkInterceptorAop.interceptor(joinPoint);
        // 判断 @Check 注解里的 checkParam 属性，如果为 false，则执行 doCheckParamInterceptor 的拦截逻辑。
        if (!checkParamEnabled(joinPoint)) {
            doCheckParamInterceptor(joinPoint);
        }
    }

    /**
     * 选中已启用参数
     *
     * @param joinPoint 连接点
     * @return boolean
     */
    private boolean checkParamEnabled(JoinPoint joinPoint) {
        // 获取当前切入点方法的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Check checkAnnotation = method.getAnnotation(Check.class);
        // 如果注解存在，并且 checkParam 为 false，则返回 false
        return checkAnnotation.checkParam();
    }

    /**
     * 在 controller 包下的方法执行前，执行 CheckParamInterceptorAop 的拦截逻辑。
     *
     * @param joinPoint 切入点对象，包含方法的相关信息。
     * @throws BusinessException 拦截到业务异常时抛出 BusinessException。
     */
    @Order(3)
    @Before("paramCheckPointcut()")
    public void doCheckParamInterceptor(JoinPoint joinPoint) throws BusinessException {
        checkParamInterceptorAop.interceptor(joinPoint);
    }
}