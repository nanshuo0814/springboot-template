package com.nanshuo.project.annotation;

import java.lang.annotation.*;

/**
 * 检查
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
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

