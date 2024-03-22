package com.nanshuo.springboot.aop;

import com.nanshuo.springboot.annotation.Check;
import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import com.nanshuo.springboot.model.enums.user.UserRoleEnums;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * 自定义检查拦截器 AOP
 * 该类用于拦截带有 @Check 注解的方法，执行参数和身份验证的校验逻辑。
 * 支持对方法参数的非空、长度、正则表达式等进行校验，以及对身份的权限验证。
 *
 * @author nanshuo
 * @date 2023/12/30 20:36:34
 */
@Slf4j
@Component("CustomCheckAopMethod")
public class CustomCheckAopMethod {

    private final UserService userService;
    public CustomCheckAopMethod(UserService userService) {
        this.userService = userService;
    }

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
            // 获取身份验证角色
            String authRole = checkAnnotation != null ? checkAnnotation.checkAuth() : "";
            // 如果指定了身份验证角色
            if (StringUtils.isNotBlank(authRole)) {
                // 执行身份验证
                checkAuth(authRole);
            }
            // 如果存在 @Check 注解并且需要进行参数验证，则执行参数验证
            if (checkAnnotation != null && checkAnnotation.checkParam()) {
                validateParams(method, arguments);
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
     * 检查权限身份
     *
     * @param checkAuth 身份(user/admin)
     * @throws BusinessException 业务异常
     */
    private void checkAuth(String checkAuth) throws BusinessException {
        // 获取当前请求的 HttpServletRequest
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 检查用户是否具有必要的权限
        if (StringUtils.isNotBlank(checkAuth)) {
            UserRoleEnums mustUserRoleEnums = UserRoleEnums.getEnumByValue(checkAuth);
            if (mustUserRoleEnums == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            String userRole = loginUser.getUserRole();
            // 如果用户被封号，直接拒绝
            if (UserRoleEnums.BAN.equals(mustUserRoleEnums)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 如果需要管理员权限，但用户不是管理员，拒绝
            if (UserRoleEnums.ADMIN.equals(mustUserRoleEnums)) {
                if (!checkAuth.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                }
            }
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
     * 验证参数
     *
     * @param method    被验证的方法。
     * @param arguments 方法参数数组。
     * @throws BusinessException 如果参数验证失败，抛出 BusinessException 异常。
     */
    private static void validateParams(Method method, Object[] arguments) throws BusinessException {
        // 获取方法的参数列表
        Parameter[] parameters = method.getParameters();
        // 遍历参数列表进行验证
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            CheckParam checkParam = parameter.getAnnotation(CheckParam.class);
            Object argument = arguments[i];
            // 如果参数是基本数据类型，则进行基本数据类型验证
            if (isBasicType(parameter.getType())) {
                // 如果存在 @CheckParam 注解，则根据注解配置进行验证
                if (checkParam != null) {
                    if (checkParam.required() == NumberConstant.DEFAULT_VALUE
                            && checkParam.minLength() == NumberConstant.DEFAULT_VALUE
                            && checkParam.maxLength() == NumberConstant.DEFAULT_VALUE
                            && checkParam.minValue() == NumberConstant.DEFAULT_VALUE
                            && checkParam.maxValue() == NumberConstant.DEFAULT_VALUE
                            && checkParam.regex().equals(UserRegexEnums.NO)) {
                        // 如果验证条件允许跳过不校验，则直接退出本次循环
                        continue;
                    }
                    checkBasicValue(argument, checkParam);
                } else {
                    // 这种情况是有@Check注解,且方法的参数没有@CheckParame注解,直接校验是否为空
                    if (ObjectUtils.isEmpty(argument)) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL, "请求的参数" + parameter.getName() + "不能为空");
                    }
                }
            } else {
                // 对象类型
                // 获取参数的泛型类型名称
                String typeName = parameter.getParameterizedType().getTypeName();
                Class<?> aClass;
                try {
                    aClass = Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                // @CheckParam不为空
                if (checkParam != null) {
                    // @CheckParam没有设置required或设置为false
                    if (checkParam.required() != NumberConstant.TRUE_ONE_VALUE) {
                        // 如果验证条件允许跳过，则直接退出本次循环
                        continue;
                    }
                    checkObjValue(aClass, argument);
                } else {
                    // 没有@CheckParam注解,直接校验
                    checkObjValue(aClass, argument);
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
    private static boolean isBasicType(Class<?> type) {
        return ClassUtils.isPrimitiveOrWrapper(type) || type.equals(String.class) || Number.class.isAssignableFrom(type);
    }

    /**
     * 验证基本数据类型的值，包括非空判断、长度范围检查和正则表达式匹配。
     *
     * @param value      待验证的值。
     * @param checkParam 参数上的 @CheckParam 注解，包含验证条件。
     * @throws BusinessException 如果验证失败，抛出对应的异常（PARAMS_NULL、PARAMS_LENGTH_ERROR 或 PARAMS_FORMAT_ERROR）。
     */
    private static void checkBasicValue(Object value, CheckParam checkParam) throws BusinessException {
        // 检查是否必填且值为空
        if (checkParam.required() != NumberConstant.FALSE_ZERO_VALUE && ObjectUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, checkParam.alias() + "不能为空");
        }
        // 如果值不为空
        if (!ObjectUtils.isEmpty(value)) {
            // 如果值为整数类型(Integer 或 Long)
            if (value instanceof Integer || value instanceof Long) {
                long longValue = value instanceof Integer ? ((Integer) value).longValue() : (Long) value;
                // 如果设置了最小值
                if (checkParam.minValue() != NumberConstant.DEFAULT_VALUE && longValue < checkParam.minValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_VALUE_ERROR, checkParam.alias() + "必须大于等于" + checkParam.minValue());
                }
                // 如果设置了最大值
                if (checkParam.maxValue() != NumberConstant.DEFAULT_VALUE && longValue > checkParam.maxValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_VALUE_ERROR, checkParam.alias() + "必须小于等于" + checkParam.maxValue());
                }
            } else { // 其他类型
                String valueString = String.valueOf(value);
                int length = valueString.length();
                // 如果设置了最小长度
                if (checkParam.minLength() != NumberConstant.DEFAULT_VALUE && checkParam.minLength() > length) {
                    throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, checkParam.alias() + "长度必须大于等于" + checkParam.minLength());
                }
                // 如果设置了最大长度
                if (checkParam.maxLength() != NumberConstant.DEFAULT_VALUE && checkParam.maxLength() < length) {
                    throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, checkParam.alias() + "长度必须小于等于" + checkParam.maxLength());
                }
                // 如果设置了正则表达式，并且值不符合要求
                if (!StringUtils.isEmpty(checkParam.regex().getRegex()) && !RegexUtils.matches(checkParam.regex(), valueString)) {
                    throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, checkParam.alias() + "非法/错误");
                }
            }
        }
    }

    /**
     * 验证对象类型的值，包括字段的非空判断和递归调用 checkBasicValue 方法进行字段值的验证。
     *
     * @param value  待验证的对象值。
     * @param aClass 一个类
     * @throws BusinessException 如果验证失败，抛出 PARAMS_NULL 异常。
     */
    private static void checkObjValue(Class<?> aClass, Object value) throws BusinessException {
        try {
            // 获取对象的所有字段
            Field[] fields = aClass.getDeclaredFields();
            // 遍历字段进行验证
            for (Field field : fields) {
                // 获取字段上的 @CheckParam 注解
                CheckParam checkParam = field.getAnnotation(CheckParam.class);
                // 设置字段可访问，使得可以获取到私有字段的值
                field.setAccessible(true);
                // 获取字段的值
                Object fieldValue = field.get(value);
                // 获取字段的类型
                Class<?> fieldType = field.getType();
                // 如果字段是基本数据类型
                if (isBasicType(fieldType)) {
                    // 如果字段上存在 @CheckParam 注解，则进行字段值的验证
                    if (checkParam != null) {
                        // 如果验证条件要求不是必须的，且没有校验规则，则跳过后续验证
                        if (checkParam.required() == NumberConstant.FALSE_ZERO_VALUE && checkParam.minLength() == NumberConstant.DEFAULT_VALUE && checkParam.maxLength() == NumberConstant.DEFAULT_VALUE && checkParam.regex().equals(UserRegexEnums.NO)) {
                            continue;
                        }
                        // 验证字段值
                        checkBasicValue(fieldValue, checkParam);
                    } else {
                        // 如果字段上不存在 @CheckParam 注解，则检查字段值是否为空
                        if (ObjectUtils.isEmpty(fieldValue)) {
                            throw new BusinessException(ErrorCode.PARAMS_NULL, "请求的参数" + field.getName() + "不能为空");
                        }
                    }
                } else {
                    // 如果字段不是基本数据类型,递归调用 checkObjValue 方法进行验证
                    checkObjValue(fieldType, fieldValue);
                }
            }
        } catch (IllegalAccessException e) {
            // 处理异常，记录错误日志并抛出 PARAMS_ERROR 异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        }
    }

}