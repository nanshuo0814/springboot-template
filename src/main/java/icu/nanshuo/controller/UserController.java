package icu.nanshuo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.nanshuo.annotation.Verify;
import icu.nanshuo.common.ApiResponse;
import icu.nanshuo.common.ApiResult;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.config.WxOpenConfig;
import icu.nanshuo.constant.PageConstant;
import icu.nanshuo.constant.UserConstant;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.IdRequest;
import icu.nanshuo.model.dto.IdsRequest;
import icu.nanshuo.model.dto.user.*;
import icu.nanshuo.model.vo.user.UserLoginVO;
import icu.nanshuo.model.vo.user.UserVO;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.ThrowUtils;
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
 * @date 2024/07/26
 */
@Slf4j
//@Api(tags = "用户模块")
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
    @Verify(checkParam = true)
    public ApiResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ApiResult.success(userService.userRegister(userRegisterRequest), "注册成功！");
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录Request
     * @return {@code ApiResponse<UserLoginVO>}
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    @Verify(checkParam = true)
    public ApiResponse<UserLoginVO> userLogin(HttpServletRequest request, @RequestBody UserLoginRequest userLoginRequest) {
        // Todo 登录类型实现
        return ApiResult.success(userService.userLogin(request, userLoginRequest), "登录成功！");
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
            return ApiResult.success(userService.userLoginByMpOpen(userInfo, request), "登录成功！");
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
     * 用户注销（需要 user 权限）
     *
     * @param request 请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户注销")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<String> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIfNull(request);
        return ApiResult.success(userService.userLogout(request), "退出成功！");
    }

    /**
     * 用户密码重置(邮箱验证码)
     *
     * @param userPwdResetByEmailRequest 用户密码重置Request
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/reset/email")
    @ApiOperation(value = "邮箱验证码进行密码重置")
    @Verify(checkParam = true)
    public ApiResponse<Boolean> userPasswordResetByEmail(HttpServletRequest request, @RequestBody UserPwdResetByEmailRequest userPwdResetByEmailRequest) {
        return ApiResult.success(userService.userPasswordResetByEmail(request, userPwdResetByEmailRequest), "密码重置成功！");
    }

    /**
     * 用户通过电子邮件重置pwd（分步骤重置密码）
     * 先验证邮箱是否存在，再进行下一步验证邮箱验证码是否正确，最后再跳到输入新密码的页面
     *
     * @param userResetPwdByEmailStepRequest 用户重置pwd请求
     * @return {@link ApiResponse }<{@link String }>
     */
    @PostMapping("/pwd/reset/email/step")
    @ApiOperation(value = "邮箱验证码分步重置密码")
    @Verify(checkParam = true)
    public ApiResponse<Integer> userResetPwdByEmailStep(@RequestBody UserResetPwdByEmailStepRequest userResetPwdByEmailStepRequest) {
        return ApiResult.success(userService.userResetPwdByEmail(userResetPwdByEmailStepRequest), "密码重置成功！");
    }

    /**
     * 用户自己修改密码(需要 user 权限)
     *
     * @param request                   请求
     * @param userPasswordUpdateRequest 用户密码更新Request
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/update")
    @ApiOperation(value = "用户自行修改密码(需要 user 权限)")
    @Verify(checkParam = true, checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Boolean> userPasswordUpdateByMyself(HttpServletRequest request, @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        return ApiResult.success(userService.userPasswordUpdateByMyself(request, userPasswordUpdateRequest), "密码修改成功！");
    }

    /**
     * 用户密码重置(需要 admin 权限)
     *
     * @param idRequest id请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/pwd/reset/admin")
    @Verify(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "重置用户密码（需要 admin 权限）")
    public ApiResponse<Boolean> userPasswordResetByAdmin(@RequestBody IdRequest idRequest) {
        return ApiResult.success(userService.userPasswordResetByAdmin(idRequest.getId()), "用户密码重置成功！");
    }

    // region 增删改查

    /**
     * 添加用户(需要 admin 权限)
     *
     * @param userAddRequest 用户添加Request
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加用户（需要 admin 权限）")
    @Verify(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        return ApiResult.success(userService.addUser(userAddRequest, userService.getLoginUser(request)), "添加用户成功！");
    }

    /**
     * 删除用户(需要 admin 权限)
     *
     * @param idRequest 删除请求
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户（需要 admin 权限）")
    @Verify(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<Long> deleteUser(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        Long userId = idRequest.getId();
        ThrowUtils.throwIf(userService.deleteUser(userId, user) < 1, ErrorCode.SYSTEM_ERROR, "用户不存在或已删除！");
        return ApiResult.success(userId, "删除用户成功！");
    }

    /**
     * 删除用户批处理
     *
     * @param idsRequest id请求
     * @param request   请求
     * @return {@link ApiResponse }<{@link Long }>
     */
    @PostMapping("/delete/batch")
    @ApiOperation(value = "批量删除用户（需要 admin 权限）")
    @Verify(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<List<Long>> deleteUserBatch(@RequestBody IdsRequest idsRequest, HttpServletRequest request) {
        if (idsRequest == null || idsRequest.getIds().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 执行批量删除
        List<Long> ids = idsRequest.getIds();
        List<Long> userIds = userService.deleteUserBatch(ids);
        return ApiResult.success(userIds, "批量删除用户成功！");
    }

    /**
     * 修改用户信息(需要 user 权限)
     *
     * @param userUpdateRequest 用户更新Request
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改用户信息（需要 user 权限）")
    @Verify(checkParam = true, checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Integer> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Integer result = userService.updateUserInfo(userUpdateRequest, request);
        ThrowUtils.throwIf(result < 1, ErrorCode.SYSTEM_ERROR, "修改用户信息失败!");
        return ApiResult.success(result, "修改用户信息成功！");
    }

    /**
     * 按id获取用户(需要 admin 权限)
     *
     * @param idRequest id请求
     * @return {@code ApiResponse<User>}
     */
    @GetMapping("/get")
    @ApiOperation(value = "按id获取用户（需要 admin 权限）")
    @Verify(checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<User> getUserById(IdRequest idRequest) {
        User user = userService.getById(idRequest.getId());
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在或已删除！");
        return ApiResult.success(user);
    }

    /**
     * 按id获取用户封装VO
     *
     * @param idRequest id请求
     * @return {@link ApiResponse }<{@link User }>
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "按id获取用户封装VO")
    public ApiResponse<UserVO> getUserVOById(IdRequest idRequest) {
        User user = userService.getById(idRequest.getId());
        UserVO userVO = userService.getUserVO(user);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在或已删除！");
        return ApiResult.success(userVO);
    }

    /**
     * 获取查询用户列表Page(需要 admin 权限)
     *
     * @param userQueryRequest 用户查询请求
     * @return {@link ApiResponse }<{@link Page }<{@link User }>>
     */
    @PostMapping("/list/page")
    @Verify(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "获取用户分页信息（需要 admin 权限）")
    public ApiResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
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
    @ApiOperation(value = "获取用户分页视图")
    public ApiResponse<Page<UserVO>> getUserVoListByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVoPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVoPage.setRecords(userVOList);
        return ApiResult.success(userVoPage);
    }

    // endregion

}