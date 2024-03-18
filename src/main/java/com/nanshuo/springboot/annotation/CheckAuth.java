package com.nanshuo.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查身份验证
 *
 * @author nanshuo
 * @date 2023/12/31 00:10:46
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuth {

    /**
     * 必须有某个角色,(user,admin,ban)
     *
     * @return {@code String}
     */
    String mustRole() default "";

}

