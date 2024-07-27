package com.nanshuo.icu.annotation;


import com.nanshuo.icu.constant.NumberConstant;
import com.nanshuo.icu.constant.VerifyParamRegexConstant;

import java.lang.annotation.*;

/**
 * 检查参数注解，用在方法参数或字段上
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CheckParam {

    /**
     * 正则表达式
     *
     * @return {@link String }
     */
    String regex() default VerifyParamRegexConstant.NONE;

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
