package com.xiaoyuer.springboot.annotation;


import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.model.enums.user.UserRegexEnums;

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
     * 正则表达式错误消息
     *
     * @return {@code String}
     */
    String regexErrorMsg() default "请求的参数格式不正确";

    /**
     * null错误消息
     *
     * @return {@code String}
     */
    String nullErrorMsg() default "请求的参数为空";

    /**
     * 长度错误信息
     *
     * @return {@code String}
     */
    String lenghtErrorMsg() default "请求的参数长度不符合要求";

}
