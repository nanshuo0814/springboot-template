package com.xiaoyuer.springboot.annotation;


import com.xiaoyuer.springboot.model.enums.VerifyRegexEnums;

import java.lang.annotation.*;

/**
 * 检查参数
 *
 * @author 小鱼儿
 * @date 2023/12/23 20:25:31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface CheckParam {

    /**
     * 正则表达式
     *
     * @return {@code VerifyRegexEnums}
     */
    VerifyRegexEnums regex() default VerifyRegexEnums.NO;

    /**
     * 最小长度
     *
     * @return int
     */
    int minLength() default -1;

    /**
     * 最大长度
     *
     * @return int
     */
    int maxLength() default -1;

    /**
     * 必填
     *
     * @return boolean
     */
    boolean required() default false;
}
