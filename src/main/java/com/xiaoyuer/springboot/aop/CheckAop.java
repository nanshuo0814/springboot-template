package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.ErrorCode;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 检查AOP
 *
 * @author 小鱼儿
 * @date 2023/12/24 14:17:40
 */
@Slf4j
@Aspect
@Component("CheckAop")
public class CheckAop {

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.xiaoyuer.springboot.annotation.Check)")
    public void pointcut() {
    }

    /**
     * 在方法执行之前进行全局拦截
     *
     * @param joinPoint joinPoint对象，包含了被拦截的方法的信息
     * @throws BusinessException 如果全局拦截器抛出了BusinessException异常
     */
    @Before("pointcut()")
    public void interceptor(JoinPoint joinPoint) throws BusinessException {
        try {
            // 获取当前方法的目标对象
            Object target = joinPoint.getTarget();
            // 获取当前方法的参数数组
            Object[] arguments = joinPoint.getArgs();
            // 获取当前方法的名称
            String methodName = joinPoint.getSignature().getName();
            // 获取当前方法的参数类型数组
            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            // 根据方法名称和参数类型数组，在目标对象的类中获取对应的方法
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            // 判断获取到的方法是否有Check注解
            Check interceptor = method.getAnnotation(Check.class);
            // 如果没有，则直接返回
            if (null == interceptor) {
                return;
            }
            // 校验参数
            if (interceptor.checkParam()) {
                validateParams(method, arguments);
            }
            // 校验权限
            //if (interceptor.checkAuth()) {
            //    validateAuth(method, arguments);
            //}
        } catch (BusinessException e) {
            log.error("拦截器捕获到业务异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw e;
        } catch (Throwable e) {
            log.error("拦截器捕获到系统异常 - 方法: {}, 参数: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 验证参数
     *
     * @param method    方法
     * @param arguments 参数数组
     */
    private void validateParams(Method method, Object[] arguments) throws BusinessException {
        // 获取方法参数
        Parameter[] parameters = method.getParameters();
        // 遍历参数数组
        for (int i = 0; i < parameters.length; i++) {

            // 获取当前参数
            Parameter parameter = parameters[i];

            // 根据参数类型进行不同的校验逻辑
            if (isBasicType(parameter.getType())) {
                // 基本数据类型
                // 获取传入的参数值
                Object value = arguments[i];
                if (value == null) {
                    throw new BusinessException(ErrorCode.PARAMS_NULL);
                }
                // 获取参数注解
                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);
                if (verifyParam != null) {
                    // 如果参数不是必须的,不检查该参数
                    if (!verifyParam.required()) {
                        break;
                    } else {
                        checkBasicValue(value, verifyParam);
                    }
                } else {
                    if (value == "") {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }
                }
            } else {
                // 如果传递的是对象
                Object value = arguments[i];
                // 如果对象为空
                if (value == null) {
                    throw new BusinessException(ErrorCode.PARAMS_NULL);
                }
                // 对象不为空，获取参数注解
                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);
                if (verifyParam != null) {
                    // 如果参数不是必须的,不检查该参数
                    if (!verifyParam.required()) {
                        break;
                    } else {
                        checkObjValue(parameter, value);
                    }
                } else {
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
        return type.isPrimitive() || type.equals(String.class) || Number.class.isAssignableFrom(type);
    }

    /**
     * 检查值是否符合验证参数的要求
     *
     * @param value      待检查的值
     * @param checkParam 验证参数对象
     * @throws BusinessException 如果值不符合验证参数的要求则抛出业务异常
     */
    private void checkBasicValue(Object value, CheckParam checkParam) throws BusinessException {
        int length = StringUtils.length(String.valueOf(value));

        // 如果值为空且验证参数要求不能为空，则抛出业务异常
        if (checkParam.required() && value == null || checkParam.required() && value.equals("")) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }

        // 如果值不为空且验证参数限制了最大长度且该长度小于实际长度，或者限制了最小长度且该长度大于实际长度，则抛出业务异常
        if (checkParam.maxLength() != -1 && checkParam.maxLength() < length || checkParam.minLength() != -1 && checkParam.minLength() > length) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR);
        }

        // 如果值不为空且验证参数的正则表达式不为空且值不符合该正则表达式要求，则抛出业务异常
        if (!StringUtils.isEmpty(checkParam.regex().getRegex()) && !RegexUtils.matches(checkParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
    }

    /**
     * 检查对象值
     *
     * @param parameter 参数
     * @param value     值
     * @throws BusinessException 业务异常
     */
    private void checkObjValue(Parameter parameter, Object value) throws BusinessException {
        // 校验参数值
        try {
            // 获取参数类型名称
            String typeName = parameter.getParameterizedType().getTypeName();
            // 根据类型名称获取Class对象
            Class<?> aClass;
            try {
                aClass = Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                log.error("未找到类: {}", typeName, e);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "未找到类: " + typeName);
            }

            // 获取Class对象的DeclaredFields数组
            Field[] fields = aClass.getDeclaredFields();
            // 遍历fields数组
            for (Field field : fields) {
                // 获取字段的CheckParam注解
                CheckParam fieldVerifyParam = field.getAnnotation(CheckParam.class);
                // 设置字段为可访问状态
                field.setAccessible(true);
                // 如果字段有CheckParam注解，则根据注解校验的规则进行参数值校验
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
                    checkBasicValue(resultValue, fieldVerifyParam);
                } else {
                    // 如果字段没有CheckParam注解，则直接校验该字段的值
                    if (field.get(value) == null) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }
                }
            }
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
