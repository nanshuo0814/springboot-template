package com.nanshuo.project.annotation;


import com.nanshuo.project.constant.NumberConstant;
import com.nanshuo.project.model.enums.user.UserRegexEnums;

import java.lang.annotation.*;

/**
 * 检查参数
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23 20:25:31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CheckParam {

    /**
     * 正则表达式
     *
     * @return {@code UserRegexEnums}
     */
    UserRegexEnums regex() default UserRegexEnums.NO;

    /**
     * 最小长度
     *
     * @return int
     */
    int minLength() default NumberConstant.DEFAULT_VALUE;

    /**
     * 最大长度
     *
     * @return int
     */
    int maxLength() default NumberConstant.DEFAULT_VALUE;

    /**
     * 必填项(0:非必填 1:必填)
     *
     * @return int
     */
    int required() default NumberConstant.DEFAULT_VALUE;

    /**
     * 最小值
     *
     * @return int
     */
    int minValue() default NumberConstant.DEFAULT_VALUE;

    /**
     * 最大值
     *
     * @return int
     */
    int maxValue() default NumberConstant.DEFAULT_VALUE;

    /**
     * 别名
     */
    String alias() default "参数";

}
