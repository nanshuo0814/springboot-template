package com.xiaoyuer.springboot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 检查参数 AOP
 *
 * @author 小鱼儿
 * @date 2023/12/23 20:39:07
 */
@Slf4j
@Aspect
@Component("CheckParameterAop")
public class CheckParameterAop {}
