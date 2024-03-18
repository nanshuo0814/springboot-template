package com.nanshuo.springboot.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring Bean 上下文获取工具
 *
 * @author nanshuo
 * @date 2024/01/07 13:45:04
 */
@Component
public class SpringBeanContextUtils implements ApplicationContextAware {

    /**
     * 应用程序上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置应用程序上下文
     *
     * @param applicationContext 应用程序上下文
     * @throws BeansException Bean异常
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        SpringBeanContextUtils.applicationContext = applicationContext;
    }

    /**
     * 通过名称获取 Bean
     *
     * @param beanName bean名称
     * @return {@code Object}
     */
    public static Object getBeanByName(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 通过 class 获取 Bean
     *
     * @param beanClass bean类
     * @return {@code T}
     */
    public static <T> T getBeanByClass(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    /**
     * 通过名称和类型获取 Bean
     *
     * @param beanName  bean名称
     * @param beanClass bean类
     * @return {@code T}
     */
    public static <T> T getBeanByNameAndClass(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }
}