package com.nanshuo.springboot.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanshuo.springboot.model.dto.user.*;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.user.admin.AdminAddUserRequest;
import com.nanshuo.springboot.model.vo.user.UserLoginVO;
import com.nanshuo.springboot.model.vo.user.UserSafetyVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author nanshuo
 * @date 2023/12/23 16:29:48
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册信息
     * @return 注册成功的用户id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录Request
     * @param request      请求
     * @return {@code UserLoginVO}
     */
    UserLoginVO userLogin(HttpServletRequest request, UserLoginRequest userLoginRequest);

    /**
     * 获取登录用户
     *
     * @param request 请求
     * @return {@code User}
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取登录用户vo
     *
     * @param user 用户
     * @return {@code UserLoginVO}
     */
    UserLoginVO getLoginUserVO(User user);

    /**
     * 按id获取用户缓存
     *
     * @param userId 用户id
     * @return {@code User}
     */
    User getUserCacheById(Long userId);

    /**
     * 将用户保存到缓存
     *
     * @param user 用户
     */
    void saveUserToCache(User user);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code String}
     */
    String userLogout(HttpServletRequest request);

    /**
     * 用户密码重置
     *
     * @param userPasswordResetRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    Boolean userPasswordReset(HttpServletRequest request, UserPasswordResetRequest userPasswordResetRequest);

    /**
     * 用户密码更新
     *
     * @param request               请求
     * @param userPasswordUpdateRequest 用户密码更新Request
     * @return {@code Boolean}
     */
    Boolean userPasswordUpdate(HttpServletRequest request, UserPasswordUpdateRequest userPasswordUpdateRequest);

    // end domain 用户登录相关

    /**
     * 添加用户
     *
     * @param adminAddUserRequest 用户添加Request
     * @return {@code Long}
     */
    Long addUser(AdminAddUserRequest adminAddUserRequest);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询Request
     * @return {@code LambdaQueryWrapper<User>}
     */
    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取用户vo
     *
     * @param records 记录
     * @return {@code List<UserVO>}
     */
    List<UserSafetyVO> getUserVO(List<User> records);

    /**
     * 获取用户vo(脱敏)
     *
     * @param user 用户
     * @return {@code UserVO}
     */
    UserSafetyVO getUserVO(User user);

    /**
     * 用户密码由admin重置
     *
     * @param userId 用户id
     * @return {@code Boolean}
     */
    Boolean userPasswordResetByAdmin(Long userId);
}
