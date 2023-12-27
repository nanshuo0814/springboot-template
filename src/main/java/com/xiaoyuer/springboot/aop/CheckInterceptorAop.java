package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import com.xiaoyuer.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 检查拦截器 AOP
 *
 * @author 小鱼儿
 * @date 2023/12/25 19:42:06
 */
@Slf4j
@Aspect
@Component("CheckInterceptorAop")
public class CheckInterceptorAop {

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.xiaoyuer.springboot.annotation.Check)")
    public void pointcut() {
    }

    /**
     * 拦截器
     *
     * @param joinPoint 加入点
     * @throws BusinessException 业务异常
     */
    @Before("pointcut()")
    public void interceptor(JoinPoint joinPoint) throws BusinessException {
        try {
            // 获取目标对象
            Object target = joinPoint.getTarget();
            // 获取方法参数
            Object[] arguments = joinPoint.getArgs();
            // 获取方法名称
            String methodName = joinPoint.getSignature().getName();
            // 获取方法参数类型
            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            // 获取方法对象
            Method method = target.getClass().getMethod(methodName, parameterTypes);

            // 检查方法是否有@Check注解
            Check interceptor = method.getAnnotation(Check.class);
            if (null == interceptor) {
                return;
            }

            // 如果有@Check注解，则检查参数
            if (interceptor.checkParam()) {
                validateParams(method, arguments);
            }
        } catch (BusinessException e) {
            // 捕获业务异常，记录日志并抛出
            log.error("拦截到业务异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw e;
        } catch (Throwable e) {
            // 捕获其他异常，记录日志并抛出业务异常
            log.error("拦截到系统异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 验证参数
     *
     * @param method    方法
     * @param arguments 参数
     * @throws BusinessException 业务异常
     */
    private void validateParams(Method method, Object[] arguments) throws BusinessException {
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        // 遍历参数
        for (int i = 0; i < parameters.length; i++) {
            // 获取当前参数
            Parameter parameter = parameters[i];

            // 判断参数类型
            if (isBasicType(parameter.getType())) {
                // 获取参数注解
                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);
                // 如果是基本数据类型
                Object value = arguments[i];
                String stringValue = String.valueOf(value);
                if (verifyParam != null) {
                    // 如果参数不是必须的，且没有其他验证规则，则跳过验证
                    if (!verifyParam.required() && verifyParam.minLength() == NumberConstant.NO_MIN_LENGTH && verifyParam.maxLength() == NumberConstant.NO_MAX_LENGTH && verifyParam.regex().equals(VerifyRegexEnums.NO)) {
                        break;
                    } else {
                        checkBasicValue(stringValue, verifyParam);
                    }
                } else {
                    // 如果参数没有注解，直接检查是否为空
                    if (StringUtils.isEmpty(stringValue)) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL, "请求参数的值为空");
                    }
                }
            } else {
                // 如果是对象类型
                Object value = arguments[i];
                if (value == null) {
                    throw new BusinessException(ErrorCode.PARAMS_NULL);
                }

                // 获取参数注解
                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);
                if (verifyParam != null) {
                    // 如果参数不是必须的，则跳过验证
                    if (!verifyParam.required()) {
                        break;
                    } else {
                        checkObjValue(parameter, value);
                    }
                } else {
                    // 如果参数没有注解，直接检查是否为空
                    checkObjValue(parameter, value);
                }
            }
        }
    }

    /**
     * 是基本类型
     *
     * @param type 类型
     * @return boolean
     */
    private boolean isBasicType(Class<?> type) {
        // 判断是否为基本数据类型
        return type.isPrimitive() || type.equals(String.class) || Number.class.isAssignableFrom(type);
    }

    /**
     * 检查基本类型值
     *
     * @param value      值
     * @param checkParam 检查参数
     * @throws BusinessException 业务异常
     */
    private void checkBasicValue(String value, CheckParam checkParam) throws BusinessException {
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

    /**
     * 检查对象类型值
     *
     * @param parameter 参数
     * @param value     值
     * @throws BusinessException 业务异常
     */
    private void checkObjValue(Parameter parameter, Object value) throws BusinessException {
        try {
            // 获取参数类型名称
            String typeName = parameter.getParameterizedType().getTypeName();
            // 根据类型名称获取Class对象
            Class<?> aClass = Class.forName(typeName);

            // 获取Class对象的DeclaredFields数组
            Field[] fields = aClass.getDeclaredFields();
            // 遍历fields数组
            for (Field field : fields) {
                // 获取字段的CheckParam注解
                CheckParam fieldVerifyParam = field.getAnnotation(CheckParam.class);
                // 设置字段为可访问状态
                field.setAccessible(true);

                // 如果字段有CheckParam注解，则根据注解进行参数值校验
                if (fieldVerifyParam != null) {
                    // 获取字段的值
                    Object resultValue = field.get(value);
                    // 如果字段的值为null且注解要求非空，则抛出异常
                    if (fieldVerifyParam.required() && resultValue == null) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }
                    // 如果字段不是必须的且字段的值为null，则跳过该字段的校验
                    if (!fieldVerifyParam.required() && resultValue == null) {
                        break;
                    }
                    // 根据字段的VerifyParam注解进行参数值校验
                    checkBasicValue(String.valueOf(resultValue), fieldVerifyParam);
                } else {
                    // 如果字段没有CheckParam注解，则直接校验该字段的值
                    if (field.get(value) == null) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            // 异常处理：未找到类
            log.error("未找到类: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未找到类: " + e.getMessage());
        } catch (IllegalAccessException e) {
            // 异常处理：无法访问字段
            log.error("无法访问字段: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法访问字段: " + e.getMessage());
        } catch (BusinessException e) {
            // 异常处理：校验参数失败
            log.error("校验参数失败 - 类: {}, 字段: {}", parameter.getDeclaringExecutable().getDeclaringClass().getName(), parameter.getName(), e);
            throw e;
        }
    }
}