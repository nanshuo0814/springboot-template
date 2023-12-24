package com.xiaoyuer.springboot.annotation;

import java.lang.annotation.*;

/**
 * 检查
 *
 * @author 小鱼儿
 * @date 2023/12/24 13:32:30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {

    /**
     * 检查参数
     *
     * @return boolean
     */
    boolean checkParam() default false;

    /**
     * 检查身份验证
     *
     * @return boolean
     */
    boolean checkAuth() default false;

}

