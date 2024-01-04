package com.xiaoyuer.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全配置
 *
 * @author 小鱼儿
 * @date 2024/01/04 13:58:15
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //TODO: 以后完善使用Security框架,例如：用户权限校验,登录校验等等，现在只使用到了CORS跨域、密码加密等等

    /**
     * 安全过滤链
     *
     * @param http http
     * @return {@code SecurityFilterChain}
     * @throws Exception 例外
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().permitAll() // 允许所有请求
                .and()
                .cors().configurationSource(corsConfigurationSource()) // 启用CORS跨域
                .and()
                .csrf().disable(); // 关闭csrf防护
        return http.build();
    }

    /**
     * b crypt密码编码器
     *
     * @return {@code BCryptPasswordEncoder}
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * cors配置源
     *
     * @return {@code CorsConfigurationSource}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有请求源(域)
        configuration.setAllowedOrigins(Arrays.asList("*"));
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        // 允许的请求头部
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
