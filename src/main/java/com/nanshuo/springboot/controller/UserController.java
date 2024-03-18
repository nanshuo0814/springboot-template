package com.nanshuo.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.springboot.annotation.Check;
import com.nanshuo.springboot.common.BaseResponse;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.common.ResultUtils;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.exception.ThrowUtils;
import com.nanshuo.springboot.model.dto.user.*;
import com.nanshuo.springboot.model.entity.User;
import com.nanshuo.springboot.model.vo.user.UserLoginVO;
import com.nanshuo.springboot.model.vo.user.UserVO;
import com.nanshuo.springboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户控制器
 *
 * @author nanshuo
 * @date 2023/12/23 16:33:46
 */
@Slf4j
@Api(tags = "普通用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // domain 用户登录相关

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册 DTO
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
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
    @ApiOperation(value = "用户登录", notes = "用户登录")
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
    @ApiOperation(value = "获取登录用户", notes = "获取登录用户")
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
    @ApiOperation(value = "用户注销", notes = "用户注销")
    public BaseResponse<String> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIfNull(request);
        return ResultUtils.success(userService.userLogout(request));
    }

    /**
     * 用户密码重置(邮箱验证码)
     *
     * @param userPasswordResetDto 用户密码重置dto
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/password/reset")
    @ApiOperation(value = "用户密码重置(邮箱验证码)", notes = "用户密码重置(邮箱验证码)")
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
    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @Check(checkParam = true, checkAuth = UserConstant.USER_ROLE)
    public BaseResponse<Boolean> updateUserPassword(HttpServletRequest request, @RequestBody UserPasswordUpdateDto userPasswordUpdateDto) {
        return ResultUtils.success(userService.userPasswordUpdate(request, userPasswordUpdateDto));
    }

    // end domain 用户登录相关

    // domain 用户的增删改查相关

    /**
     * 按页面获取用户vo列表(脱敏)
     *
     * @param userQueryDto 用户查询dto
     * @return {@code BaseResponse<Page<UserVO>>}
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "按页面获取用户vo列表(脱敏)", notes = "按页面获取用户vo列表(脱敏)")
    public BaseResponse<Page<UserVO>> getUserVoListByPage(@RequestBody UserQueryDto userQueryDto) {
        long current = userQueryDto.getCurrent();
        long size = userQueryDto.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryDto));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 修改用户信息
     *
     * @param request           请求
     * @param userUpdateInfoDto 用户更新信息dto
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/update/my")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @Check(checkAuth = UserConstant.USER_ROLE, checkParam = true)
    public BaseResponse<String> updateUserInfo(@RequestBody UserUpdateInfoDto userUpdateInfoDto,
                                                HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateInfoDto, user);
        user.setUserId(loginUser.getUserId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success("更新用户信息成功！");
    }

    // end domain 用户的增删改查相关
}