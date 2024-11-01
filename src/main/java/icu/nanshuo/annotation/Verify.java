package icu.nanshuo.annotation;

import java.lang.annotation.*;

/**
 * 验证：检查参数 + 检查权限
 * 该注解只能用在方法上指定开启某个或多个检查
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Verify {

    /**
     * 验证参数
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

