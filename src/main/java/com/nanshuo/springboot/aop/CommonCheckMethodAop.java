package com.nanshuo.springboot.aop;

import com.nanshuo.springboot.annotation.CheckParam;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;
import com.nanshuo.springboot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 共同检查方法aop
 *
 * @author nanshuo
 * @date 2024/01/03 14:40:04
 */
@Slf4j
public class CommonCheckMethodAop {

    /**
     * 验证参数
     *
     * @param method              被验证的方法。
     * @param arguments           方法参数数组。
     * @param haveCheckAnnotation 有检查注释
     * @throws BusinessException 如果参数验证失败，抛出 BusinessException 异常。
     */
    public static void validateParams(boolean haveCheckAnnotation, Method method, Object[] arguments) throws BusinessException {
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
                    if (checkParam.required() == NumberConstant.DEFAULT_VALUE && checkParam.minLength() == NumberConstant.DEFAULT_VALUE && checkParam.maxLength() == NumberConstant.DEFAULT_VALUE && checkParam.regex().equals(UserRegexEnums.NO)) {
                        // 如果验证条件允许跳过，则直接退出本次循环
                        continue;
                    }
                    checkBasicValue(haveCheckAnnotation, argument, checkParam);
                } else {
                    // 只需校验从@Check注解过来的即可
                    if (haveCheckAnnotation && ObjectUtils.isEmpty(argument)) {
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
                // 有@CheckParam注解
                if (checkParam != null) {
                    // 从@CheckParam过来的且不是必填或从@Check注解过来的且也不是必填
                    if (!haveCheckAnnotation && checkParam.required() != NumberConstant.TRUE_VALUE || haveCheckAnnotation && checkParam.required() == NumberConstant.FALSE_VALUE) {
                        // 如果验证条件允许跳过，则直接退出本次循环
                        continue;
                    }
                    checkObjValue(haveCheckAnnotation, aClass, argument);
                } else {
                    // 没有@CheckParam注解
                    // 从@CheckParam过来的
                    if (!haveCheckAnnotation) {
                        continue;
                    }
                    // 从@Check注解过来的
                    checkObjValue(true, aClass, argument);
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
    private static void checkBasicValue(boolean haveCheckAnnotation, Object value, CheckParam checkParam) throws BusinessException {

        // 从@Check注解过来的且是必填的
        if (haveCheckAnnotation && checkParam.required() != NumberConstant.FALSE_VALUE && ObjectUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, checkParam.alias() + "不能为空");
        }

        // 如果验证条件要求非空，且值为空，则抛出 PARAMS_NULL 异常
        if (checkParam.required() == NumberConstant.TRUE_VALUE && ObjectUtils.isEmpty(value)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, checkParam.alias() + "不能为空");
        }

        // 如果值不为空
        if (!ObjectUtils.isEmpty(value)) {
            // 获取值的长度
            int length = value.toString().length();
            // 如果验证条件要求最大长度大于 0 且小于实际长度，或者最小长度大于 0 且大于实际长度，则抛出 PARAMS_LENGTH_ERROR 异常
            if (checkParam.minLength() != NumberConstant.DEFAULT_VALUE && checkParam.maxLength() != NumberConstant.DEFAULT_VALUE) {
                if (checkParam.minLength() > length && checkParam.maxLength() < length) {
                    throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, checkParam.alias() + "长度必须在" + checkParam.minLength() + "-" + checkParam.maxLength() + "之间");
                }
            }
            if (checkParam.minLength() != NumberConstant.DEFAULT_VALUE && checkParam.minLength() > length) {
                throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, checkParam.alias() + "长度必须大于" + checkParam.minLength());
            }
            if (checkParam.minLength() != NumberConstant.DEFAULT_VALUE && checkParam.maxLength() < length) {
                throw new BusinessException(ErrorCode.PARAMS_LENGTH_ERROR, checkParam.alias() + "长度必须小于" + checkParam.maxLength());
            }
            // 如果验证条件要求的正则表达式不为空，且值不符合该正则表达式要求，则抛出 PARAMS_FORMAT_ERROR 异常
            if (!StringUtils.isEmpty(checkParam.regex().getRegex()) && !RegexUtils.matches(checkParam.regex(), String.valueOf(value))) {
                throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR, checkParam.alias() + "非法/错误");
            }
        }
    }

    /**
     * 验证对象类型的值，包括字段的非空判断和递归调用 checkBasicValue 方法进行字段值的验证。
     *
     * @param value               待验证的对象值。
     * @param aClass              一个类
     * @param haveCheckAnnotation 有检查注释
     * @throws BusinessException 如果验证失败，抛出 PARAMS_NULL 异常。
     */
    private static void checkObjValue(boolean haveCheckAnnotation, Class<?> aClass, Object value) throws BusinessException {
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
                        if (checkParam.required() == NumberConstant.FALSE_VALUE && checkParam.minLength() == NumberConstant.DEFAULT_VALUE && checkParam.maxLength() == NumberConstant.DEFAULT_VALUE && checkParam.regex().equals(UserRegexEnums.NO)) {
                            continue;
                        }
                        // 验证字段值
                        checkBasicValue(haveCheckAnnotation, fieldValue, checkParam);
                    } else {
                        // 如果字段上不存在 @CheckParam 注解，则检查字段值是否为空，为空则抛出 PARAMS_NULL 异常
                        if (ObjectUtils.isEmpty(fieldValue)) {
                            throw new BusinessException(ErrorCode.PARAMS_NULL, "请求的参数" + fieldType.getName() + "不能为空");
                        }
                    }
                } else {
                    // 如果字段不是基本数据类型,递归调用 checkObjValue 方法进行验证
                    checkObjValue(haveCheckAnnotation, fieldType, fieldValue);
                }
            }
        } catch (IllegalAccessException e) {
            // 处理异常，记录错误日志并抛出 PARAMS_ERROR 异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
        }
    }

}
