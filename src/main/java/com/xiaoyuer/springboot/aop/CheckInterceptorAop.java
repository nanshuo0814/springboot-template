package com.xiaoyuer.springboot.aop;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;
import com.xiaoyuer.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
            // 提取被拦截方法的相关信息
            Object target = joinPoint.getTarget();
            // 方法的目标对象
            Object[] arguments = joinPoint.getArgs();
            // 方法的参数
            String methodName = joinPoint.getSignature().getName();
            // 被拦截方法的名称
            Method method = getMethod(target, methodName, joinPoint);
            // 表示被拦截方法的 Method 对象

            // 检查是否存在 @Check 注解并且需要进行参数验证
            Check checkAnnotation = method.getAnnotation(Check.class);
            CheckAuth checkAuth = method.getAnnotation(CheckAuth.class);

            if (checkAnnotation != null && checkAnnotation.checkParam()) {
                // 如果存在 @Check 注解并且需要进行参数验证，则执行参数验证
                validateParams(method, arguments);
            }

            String authRole = checkAnnotation != null ? checkAnnotation.checkAuth() : "";
            if (StringUtils.isNotBlank(authRole) && checkAuth == null) {
                // 如果指定了身份验证角色并且未存在 @CheckAuth 注解，
                // 将 authRole 传递给 AuthInterceptor
                authInterceptor.doInterceptor(authRole);
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

    /**
     * 获取目标对象的指定方法。
     *
     * @param target     目标对象，即方法所属的对象。
     * @param methodName 方法名称。
     * @param joinPoint  切面信息，用于获取方法签名。
     * @return 方法对象。
     * @throws NoSuchMethodException 如果找不到指定方法，则抛出此异常。
     */
    private Method getMethod(Object target, String methodName, JoinPoint joinPoint) throws NoSuchMethodException {
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        return target.getClass().getMethod(methodName, parameterTypes);
    }

    /**
     * 验证方法参数的私有方法。
     *
     * @param method    被验证的方法。
     * @param arguments 方法参数数组。
     * @throws BusinessException 如果参数验证失败，抛出 BusinessException 异常。
     */
    private void validateParams(Method method, Object[] arguments) throws BusinessException {
        // 获取方法的参数列表
        Parameter[] parameters = method.getParameters();

        // 遍历参数列表进行验证
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (isBasicType(parameter.getType())) {
                // 如果参数是基本数据类型，则进行基本数据类型验证
                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);
                Object value = arguments[i];
                String stringValue = String.valueOf(value);

                if (verifyParam != null) {
                    // 如果存在 @CheckParam 注解，则根据注解配置进行验证
                    if (!verifyParam.required() && verifyParam.minLength() == NumberConstant.NO_MIN_LENGTH && verifyParam.maxLength() == NumberConstant.NO_MAX_LENGTH && verifyParam.regex().equals(VerifyRegexEnums.NO)) {
                        // 如果验证条件允许跳过，则直接退出循环
                        break;
                    } else {
                        // 否则，进行基本数据类型的验证
                        checkBasicValue(stringValue, verifyParam);
                    }
                } else {
                    // 如果参数不带 @CheckParam 注解，则检查参数是否为空
                    if (StringUtils.isEmpty(stringValue) || stringValue.equals("null")) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL, "请求参数的值为空");
                    }
                }
            } else {
                // 如果参数是对象类型，则进行对象类型验证
                Object value = arguments[i];

                if (value == null) {
                    // 如果对象为空，抛出 PARAMS_NULL 异常
                    throw new BusinessException(ErrorCode.PARAMS_NULL);
                }

                CheckParam verifyParam = parameter.getAnnotation(CheckParam.class);

                if (verifyParam != null) {
                    // 如果存在 @CheckParam 注解，则根据注解配置进行验证
                    if (!verifyParam.required()) {
                        // 如果验证条件允许跳过，则直接退出循环
                        break;
                    } else {
                        // 否则，进行对象类型的验证
                        checkObjValue(parameter, value);
                    }
                } else {
                    // 如果参数不带 @CheckParam 注解，则检查对象类型的值是否为空
                    checkObjValue(parameter, value);
                }
            }
        }
    }

    /**
     * 判断是否为基本数据类型（包括原始类型、String 类型以及 Number 类型）。
     *
     * @param type 待判断的类型。
     * @return 如果是基本数据类型，则返回 true；否则返回 false。
     */
    private boolean isBasicType(Class<?> type) {
        return type.isPrimitive() || type.equals(String.class) || Number.class.isAssignableFrom(type);
    }

    /**
     * 验证基本数据类型的值，包括非空判断、长度范围检查和正则表达式匹配。
     *
     * @param value      待验证的值。
     * @param checkParam 参数上的 @CheckParam 注解，包含验证条件。
     * @throws BusinessException 如果验证失败，抛出对应的异常（PARAMS_NULL、PARAMS_LENGTH_ERROR 或 PARAMS_FORMAT_ERROR）。
     */
    private void checkBasicValue(String value, CheckParam checkParam) throws BusinessException {
        // 获取值的长度
        int length = StringUtils.length(value);

        // 如果验证条件要求非空，且值为空，则抛出 PARAMS_NULL 异常
        if (checkParam.required() && StringUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }

        // 如果验证条件要求最大长度大于 0 且小于实际长度，或者最小长度大于 0 且大于实际长度，则抛出 PARAMS_LENGTH_ERROR 异常
        if ((checkParam.maxLength() != NumberConstant.NO_MAX_LENGTH && checkParam.maxLength() < length) ||
                (checkParam.minLength() != NumberConstant.NO_MIN_LENGTH && checkParam.minLength() > length)) {
            throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR);
        }

        // 如果验证条件要求的正则表达式不为空，且值不符合该正则表达式要求，则抛出 PARAMS_FORMAT_ERROR 异常
        if (!StringUtils.isEmpty(checkParam.regex().getRegex()) && !RegexUtils.matches(checkParam.regex(), value)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
    }

    /**
     * 验证对象类型的值，包括字段的非空判断和递归调用 checkBasicValue 方法进行字段值的验证。
     *
     * @param parameter 参数，包含参数类型和泛型信息。
     * @param value     待验证的对象值。
     * @throws BusinessException 如果验证失败，抛出 PARAMS_NULL 异常。
     */
    private void checkObjValue(Parameter parameter, Object value) throws BusinessException {
        try {
            // 获取参数的泛型类型名称
            String typeName = parameter.getParameterizedType().getTypeName();
            // 根据泛型类型名称获取 Class 对象
            Class<?> aClass = Class.forName(typeName);
            // 获取对象的所有字段
            Field[] fields = aClass.getDeclaredFields();

            // 遍历字段进行验证
            for (Field field : fields) {
                // 获取字段上的 @CheckParam 注解
                CheckParam fieldVerifyParam = field.getAnnotation(CheckParam.class);
                // 设置字段可访问，使得可以获取到私有字段的值
                field.setAccessible(true);

                if (fieldVerifyParam != null) {
                    // 如果字段上存在 @CheckParam 注解，则进行字段值的验证
                    Object resultValue = field.get(value);

                    // 如果验证条件要求非空，且字段值为空，则抛出 PARAMS_NULL 异常
                    if (fieldVerifyParam.required() && resultValue == null) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }

                    // 如果验证条件要求不是必须的，且字段值为空，则跳过后续验证
                    if (!fieldVerifyParam.required() && resultValue == null) {
                        break;
                    }

                    // 递归调用 checkBasicValue 方法，验证字段值
                    checkBasicValue(String.valueOf(resultValue), fieldVerifyParam);
                } else {
                    // 如果字段上不存在 @CheckParam 注解，则检查字段值是否为空，为空则抛出 PARAMS_NULL 异常
                    if (field.get(value) == null) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL);
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            // 处理异常，记录错误日志并抛出 PARAMS_ERROR 异常
            log.error("参数校验失败 - 类: {}, 字段: {}", parameter.getDeclaringExecutable().getDeclaringClass().getName(), parameter.getName(), e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        }
    }
}