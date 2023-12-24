package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestCheckController {

    /**
     * @Check + 基本类型
     */
    @GetMapping("/test1")
    @Check(checkParam = true)
    public BaseResponse<Long> test1(@CheckParam(required = false) Long param) {
        return ResultUtils.success(param);
    }

    /**
     * @Check + 基本类型 + @CheckParam(required = false)
     */
    @GetMapping("/test2")
    @Check(checkParam = true)
    public BaseResponse<Long> test2(@CheckParam(required = false) Long param, String param2) {
        return ResultUtils.success(param);
    }

    /**
     * @Check + 基本类型 + @CheckParam(required = true)
     */
    @GetMapping("/test3")
    @Check(checkParam = true)
    public BaseResponse<Long> test3(Long param, @CheckParam(required = true) String param2) {
        return ResultUtils.success(param);
    }

    /**
     * @Check + 对象类型
     */
    @GetMapping("/test4")
    @Check(checkParam = true)
    public BaseResponse<TestDto> test4(@CheckParam(required = true) TestDto testDto) {
        return ResultUtils.success(testDto);
    }

    @PostMapping("/test5")
    @Check(checkParam = true)
    public BaseResponse<TestDto> test5(@CheckParam(required = true) TestDto testDto) {
        return ResultUtils.success(testDto);
    }
}
