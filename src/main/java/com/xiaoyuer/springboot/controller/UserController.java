package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户控制器
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:33:46
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    /**
     * 测试调用返回类和返回工具类
     */
    @GetMapping("/test01")
    public BaseResponse<String> test() {
        return ResultUtils.success("test");
    }

    /**
     * 测试异常处理类
     */
    @GetMapping("/test02")
    public BaseResponse<String> test02() {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    /**
     * 测试抛异常工具类
     */
    @GetMapping("/test03")
    public BaseResponse<String> test03() {
        ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success("test");
    }

}
