package com.xiaoyuer.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查身份验证
 *
 * @author 小鱼儿
 * @date 2023/12/24 00:03:48
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuth {

    /**
     * 必须有某个角色
     *
     * @return {@code String}
     */
    String mustRole() default "";

}

