package com.nanshuo.springboot.annotation;


import com.nanshuo.springboot.constant.NumberConstant;
import com.nanshuo.springboot.model.enums.user.UserRegexEnums;

import java.lang.annotation.*;

/**
 * 检查参数
 *
 * @author nanshuo
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
     * 别名
     */
    String alias() default "参数";

}
