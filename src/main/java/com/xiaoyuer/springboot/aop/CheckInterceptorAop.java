package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 检查拦截器 AOP
 * 该类用于拦截带有 @Check 注解的方法，执行参数和身份验证的校验逻辑。
 * 支持对方法参数的非空、长度、正则表达式等进行校验，以及对身份的权限验证。
 *
 * @author 小鱼儿
 * @date 2023/12/30 20:36:34
 */
@Slf4j
@Component("CheckInterceptorAop")
public class CheckInterceptorAop {

    /**
     * auth拦截器
     */
    @Autowired
    private CheckAuthInterceptorAop authInterceptor;


    /**
     * 拦截器方法，用于执行参数和身份验证检查。
     *
     * @param joinPoint 被拦截方法的信息。
     * @throws BusinessException 如果验证失败，抛出 BusinessException 异常。
     */
    public void interceptor(JoinPoint joinPoint) throws BusinessException {
        try {
            // 方法的目标对象
            Object[] arguments = joinPoint.getArgs();
            // 被拦截方法的名称
            // 获取方法签名
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // 获取目标类的方法
            Method method = methodSignature.getMethod();
            // 检查是否存在 @Check 注解并且需要进行参数验证
            Check checkAnnotation = method.getAnnotation(Check.class);
            CheckAuth checkAuth = method.getAnnotation(CheckAuth.class);

            String authRole = checkAnnotation != null ? checkAnnotation.checkAuth() : "";
            // 如果指定了身份验证角色并且未存在 @CheckAuth 注解，
            if (StringUtils.isNotBlank(authRole) && checkAuth == null) {
                // 将 authRole 传递给 AuthInterceptor
                authInterceptor.doInterceptor(authRole);
            }

            // 如果存在 @Check 注解并且需要进行参数验证，则执行参数验证
            if (checkAnnotation != null && checkAnnotation.checkParam()) {
                CommonCheckMethodAop.validateParams(true, method, arguments);
            }

        } catch (BusinessException e) {
            // 处理 BusinessException，记录日志并重新抛出异常
            handleException(joinPoint, e);
        } catch (Exception e) {
            // 处理一般异常，创建 SYSTEM_ERROR BusinessException 并重新抛出异常
            handleException(joinPoint, new BusinessException(ErrorCode.SYSTEM_ERROR));
        } catch (Throwable e) {
            // 捕获其他 Throwable 并作为 RuntimeException 重新抛出
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理异常的私有方法，记录异常信息并重新抛出 BusinessException。
     *
     * @param joinPoint 切面信息，用于获取方法签名和参数。
     * @param e         异常对象，捕获的 BusinessException。
     * @throws BusinessException 重新抛出异常。
     */
    private void handleException(JoinPoint joinPoint, BusinessException e) throws BusinessException {
        // 记录异常信息，包括方法签名、参数和异常堆栈
        log.error("拦截到异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
        throw e;  // 重新抛出 BusinessException
    }
}