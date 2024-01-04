package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.model.dto.user.UserLoginDto;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserLoginVO;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


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

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册 DTO
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/register")
    @Check(checkParam = true)
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        // 调用用户注册服务方法,返回注册结果
        return ResultUtils.success(userService.userRegister(userRegisterDto));
    }

    /**
     * 用户登录
     *
     * @param userLoginDto 用户登录dto
     * @return {@code BaseResponse<UserLoginVO>}
     */
    @PostMapping("/login")
    @Check(checkParam = true)
    public BaseResponse<UserLoginVO> userLogin(HttpServletRequest request, @RequestBody UserLoginDto userLoginDto) {
        return ResultUtils.success(userService.userLogin(request, userLoginDto));
    }

    /**
     * 获取登录用户
     *
     * @param request 请求
     * @return {@code BaseResponse<UserLoginVO>}
     */
    @GetMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

}
