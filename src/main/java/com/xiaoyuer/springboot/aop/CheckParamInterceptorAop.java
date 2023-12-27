package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component("CheckParamInterceptorAop")
public class CheckParamInterceptorAop {

    @Pointcut("execution(* com.xiaoyuer.springboot.controller.*.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void interceptor(JoinPoint joinPoint) throws BusinessException {
        try {
            // 获取目标对象
            Object target = joinPoint.getTarget();
            // 获取方法参数
            Object[] arguments = joinPoint.getArgs();
            // 获取方法对象
            Method method = getTargetMethod(joinPoint);

            // 获取方法参数的注解信息
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            // 遍历参数
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];

                // 判断参数上是否带有@CheckParam注解
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(CheckParam.class)) {
                        // 参数上有@CheckParam注解，执行相应的校验逻辑
                        Object value = arguments[i];
                        if (value == null) {
                            throw new BusinessException(ErrorCode.PARAMS_NULL);
                        }
                        String stringValue = String.valueOf(value);
                        checkParamValue(stringValue, (CheckParam) annotation);
                    }
                }
            }
        } catch (BusinessException e) {
            log.error("拦截到业务异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw e;
        } catch (Throwable e) {
            log.error("拦截到系统异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private Method getTargetMethod(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取目标类的方法
        return methodSignature.getMethod();
    }

    private void checkParamValue(String value, CheckParam checkParam) throws BusinessException {
        // 获取值的长度
        int length = StringUtils.length(value);

        // 如果值为null，且验证要求不能为空，则抛出异常
        if (checkParam.required() && StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }

        // 如果值不为空，且验证要求的最大长度大于0，且该长度小于实际长度
        // 或者验证要求的最小长度大于0，且该长度大于实际长度，则抛出异常
        if (checkParam.maxLength() != NumberConstant.NO_MAX_LENGTH && checkParam.maxLength() < length ||
                checkParam.minLength() != NumberConstant.NO_MIN_LENGTH && checkParam.minLength() > length) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR);
        }

        // 如果值不为空，且验证要求的正则表达式不为空，且值不符合该正则表达式要求，则抛出异常
        if (!StringUtils.isEmpty(checkParam.regex().getRegex()) && !RegexUtils.matches(checkParam.regex(), value)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
    }
}
