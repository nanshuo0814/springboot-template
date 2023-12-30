package com.xiaoyuer.springboot.annotation;

import java.lang.annotation.*;

/**
 * 检查
 *
 * @author 小鱼儿
 * @date 2023/12/31 00:10:34
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
     * 检查身份验证(user,admin,ban)
     *
     * @return {@code String}
     */
    String checkAuth() default "";

}

