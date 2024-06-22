package com.nanshuo.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.project.annotation.Check;
import com.nanshuo.project.common.ApiResponse;
import com.nanshuo.project.common.ApiResult;
import com.nanshuo.project.common.ErrorCode;
import com.nanshuo.project.config.WxOpenConfig;
import com.nanshuo.project.constant.UserConstant;
import com.nanshuo.project.exception.BusinessException;
import com.nanshuo.project.model.domain.User;
import com.nanshuo.project.model.dto.IdRequest;
import com.nanshuo.project.model.dto.user.*;
import com.nanshuo.project.model.vo.user.UserLoginVO;
import com.nanshuo.project.model.vo.user.UserSafetyVO;
import com.nanshuo.project.service.UserService;
import com.nanshuo.project.utils.ThrowUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 用户控制器
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2023/12/23 16:33:46
 */
@Slf4j
//@Api(tags = "普通用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private WxOpenConfig wxOpenConfig;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册 Request
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    @Check(checkParam = true)
    public ApiResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ApiResult.success(userService.userRegister(userRegisterRequest));
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录Request
     * @return {@code ApiResponse<UserLoginVO>}
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @Check(checkParam = true)
    public ApiResponse<UserLoginVO> userLogin(HttpServletRequest request, @RequestBody UserLoginRequest userLoginRequest) {
        return ApiResult.success(userService.userLogin(request, userLoginRequest));
    }

    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    @ApiOperation(value = "用户微信登录")
    public ApiResponse<UserLoginVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ApiResult.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return {@code ApiResponse<UserLoginVO>}
     */
    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前登录用户")
    public ApiResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ApiResult.success(userService.getLoginUserVO(user));
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户注销")
    public ApiResponse<String> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIfNull(request);
        return ApiResult.success(userService.userLogout(request));
    }

    /**
     * 用户密码重置(邮箱验证码)
     *
     * @param userPasswordResetRequest 用户密码重置Request
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/reset/email")
    @ApiOperation(value = "用户密码重置(邮箱验证码)")
    @Check(checkParam = true)
    public ApiResponse<Boolean> userPasswordResetByEmail(HttpServletRequest request, @RequestBody UserPasswordResetRequest userPasswordResetRequest) {
        return ApiResult.success(userService.userPasswordResetByEmail(request, userPasswordResetRequest));
    }

    /**
     * 用户自己修改密码(user)
     *
     * @param request                   请求
     * @param userPasswordUpdateRequest 用户密码更新Request
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/update")
    @ApiOperation(value = "修改用户密码")
    @Check(checkParam = true, checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Boolean> userPasswordUpdateByMyself(HttpServletRequest request, @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        return ApiResult.success(userService.userPasswordUpdateByMyself(request, userPasswordUpdateRequest));
    }

    /**
     * 用户密码重置(admin)
     *
     * @param idRequest id请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/reset")
    @Check(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "重置用户密码")
    public ApiResponse<Boolean> userPasswordResetByAdmin(@RequestBody IdRequest idRequest) {
        return ApiResult.success(userService.userPasswordResetByAdmin(idRequest.getId()));
    }

    // region 增删改查

    /**
     * 添加用户(admin)
     *
     * @param userAddRequest 用户添加Request
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加用户")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        return ApiResult.success(userService.addUser(userAddRequest));
    }

    /**
     * 删除用户(admin)
     *
     * @param idRequest 删除请求
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户")
    @Check(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<Long> deleteUser(@RequestBody IdRequest idRequest) {
        Long userId = idRequest.getId();
        ThrowUtils.throwIf(!userService.removeById(userId), ErrorCode.OPERATION_ERROR, "删除用户失败,无该用户");
        return ApiResult.success(userId);
    }

    /**
     * 修改用户信息(user)
     *
     * @param userUpdateRequest 用户更新Request
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改用户信息")
    @Check(checkParam = true)
    public ApiResponse<Integer> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Integer result = userService.updateUserInfo(userUpdateRequest, request);
        ThrowUtils.throwIf(result < 1, ErrorCode.OPERATION_ERROR, "修改用户信息失败");
        return ApiResult.success(result);
    }

    /**
     * 按id获取用户(admin)
     *
     * @param idRequest id请求
     * @return {@code ApiResponse<User>}
     */
    @GetMapping("/get")
    @ApiOperation(value = "按id获取用户")
    public ApiResponse<User> getUserById(IdRequest idRequest) {
        User user = userService.getById(idRequest.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ApiResult.success(user);
    }

    /**
     * 获取查询用户列表Page(admin)
     */
    @PostMapping("/list/page")
    @Check(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取用户列表Page")
    public ApiResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        return ApiResult.success(userPage);
    }

    /**
     * 页面获取用户脱敏vo列表
     *
     * @param userQueryRequest 用户查询Request
     * @return {@code ApiResponse<Page<UserVO>>}
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "按页面获取用户vo列表(脱敏)")
    public ApiResponse<Page<UserSafetyVO>> getUserVOListByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        Page<UserSafetyVO> userSafetyVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserSafetyVO> userSafetyVOList = userService.getUserSafeVOList(userPage.getRecords());
        userSafetyVOPage.setRecords(userSafetyVOList);
        return ApiResult.success(userSafetyVOPage);
    }

    // endregion

}