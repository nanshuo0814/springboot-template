package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.exception.ThrowUtils;
import com.xiaoyuer.springboot.model.dto.user.UserLoginDto;
import com.xiaoyuer.springboot.model.dto.user.UserPasswordResetDto;
import com.xiaoyuer.springboot.model.dto.user.UserPasswordUpdateDto;
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

    // domain 用户登录相关

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

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/logout")
    public BaseResponse<String> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIfNull(request);
        return ResultUtils.success(userService.userLogout(request));
    }

    /**
     * 用户密码重置
     *
     * @param userPasswordResetDto 用户密码重置dto
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/password/reset")
    @Check(checkParam = true)
    public BaseResponse<Boolean> userPasswordReset(HttpServletRequest request, @RequestBody UserPasswordResetDto userPasswordResetDto) {
        return ResultUtils.success(userService.userPasswordReset(request, userPasswordResetDto));
    }
    //$2a$10$5qoZRJHY1PmRUC2tY0nlT.gWcQR.K1crfSgFRdd.HO9MLI5yMZUQC

    /**
     * 修改用户密码
     *
     * @param request               请求
     * @param userPasswordUpdateDto 用户密码更新dto
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/password/update")
    @Check(checkParam = true, checkAuth = "user")
    public BaseResponse<Boolean> updateUserPassword(HttpServletRequest request, @RequestBody UserPasswordUpdateDto userPasswordUpdateDto) {
        return ResultUtils.success(userService.userPasswordUpdate(request, userPasswordUpdateDto));
    }

    // end domain 用户登录相关

    // domain 用户的增删改查相关


    // end domain 用户的增删改查相关
}
