package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

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
            // 获取方法对象
            Method method = getTargetMethod(joinPoint);

            // 获取方法参数的注解信息
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            // 遍历参数
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                // 判断参数上是否带有 @CheckParam 注解
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(CheckParam.class)) {
                        CheckParam checkParamAnnotation = (CheckParam) annotation;
                        // 判断 @CheckParam 注解的 checkParam 属性是否为 true
                        if (checkParamAnnotation.required()) {
                            // 参数上有 @CheckParam 注解，并且 checkParam 属性为 true，执行相应的校验逻辑
                            Object value = arguments[i];
                            String stringValue = String.valueOf(value);
                            checkParamValue(stringValue, checkParamAnnotation);
                        }
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

    /**
     * 获取目标方法
     *
     * @param joinPoint 切入点
     * @return 目标方法
     */
    private Method getTargetMethod(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 获取目标类的方法
        return methodSignature.getMethod();
    }

    /**
     * 检查参数值
     *
     * @param value      参数值
     * @param checkParam @CheckParam 注解信息
     * @throws BusinessException 如果参数校验失败，则抛出业务异常
     */
    private void checkParamValue(String value, CheckParam checkParam) throws BusinessException {
        if (Objects.equals(value, "null")) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }
        // 获取值的长度
        int length = StringUtils.length(value);

        // 如果值为 null，且验证要求不能为空，则抛出异常
        if (checkParam.required() && StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }

        // 如果值不为空，且验证要求的最大长度大于 0，且该长度小于实际长度
        // 或者验证要求的最小长度大于 0，且该长度大于实际长度，则抛出异常
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