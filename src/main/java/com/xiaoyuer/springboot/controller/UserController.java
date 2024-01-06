package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.annotation.CheckAuth;
import com.xiaoyuer.springboot.annotation.CheckParam;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.constant.UserConstant;
import com.xiaoyuer.springboot.exception.ThrowUtils;
import com.xiaoyuer.springboot.model.dto.user.*;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserLoginVO;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户控制器
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:33:46
 */
@Slf4j
@RestController
@RequestMapping("/user")
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

    /**
     * 添加用户(管理员权限)
     *
     * @param userAddDto 用户添加dto
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/add")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDto userAddDto) {
        return ResultUtils.success(userService.addUser(userAddDto));
    }

    /**
     * 删除用户(管理员权限)
     *
     * @param userId 用户id
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/delete")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> deleteUser(@RequestBody Long userId) {
        ThrowUtils.throwIf(!userService.removeById(userId), ErrorCode.OPERATION_ERROR, "删除用户失败,无该用户");
        return ResultUtils.success(userId);
    }

    /**
     * 更新用户(管理员权限)
     *
     * @param userUpdateDto 用户更新dto
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/update")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateDto, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "修改用户信息失败,无该用户信息");
        return ResultUtils.success(user.getUserId());
    }

    /**
     * 按id获取用户(管理员权限)
     *
     * @param userId 用户id
     * @return {@code BaseResponse<User>}
     */
    @GetMapping("/get")
    @CheckAuth(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(
            @CheckParam(required = NumberConstant.TRUE_VALUE, nullErrorMsg = "用户id不能为空") Long userId) {
        User user = userService.getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(user);
    }

    // end domain 用户的增删改查相关
}