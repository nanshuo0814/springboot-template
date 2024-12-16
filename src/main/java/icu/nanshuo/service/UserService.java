package icu.nanshuo.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.user.*;
import icu.nanshuo.model.vo.user.UserLoginVO;
import icu.nanshuo.model.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
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
     * @param request          请求
     * @return {@code UserLoginVO}
     */
    UserLoginVO userLogin(HttpServletRequest request, UserLoginRequest userLoginRequest);

    /**
     * 验证电子邮件
     *
     * @param email 电子邮件
     * @return boolean
     */
    boolean validateEmail(String email);

    /**
     * 验证电子邮件代码
     *
     * @param email            电子邮件
     * @param emailCaptcha     电子邮件验证码
     * @param emailCaptchaType 电子邮件验证码类型
     * @return boolean
     */
    boolean validateEmailCode(String email, String emailCaptcha, String emailCaptchaType);

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
     * 将用户保存到redis缓存
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
     * 用户密码重置(通过邮箱重置)
     *
     * @param request                  请求
     * @param userPwdResetByEmailRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    boolean userPasswordResetByEmail(HttpServletRequest request, UserPwdResetByEmailRequest userPwdResetByEmailRequest);

    /**
     * 用户密码自行更新
     *
     * @param request                   请求
     * @param userPasswordUpdateRequest 用户密码更新Request
     * @return {@code Boolean}
     */
    boolean userPasswordUpdateByMyself(HttpServletRequest request, UserPasswordUpdateRequest userPasswordUpdateRequest);


    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询Request
     * @return {@code LambdaQueryWrapper<User>}
     */
    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 添加用户(admin)
     *
     * @param userAddRequest 用户添加Request
     * @return {@code Long}
     */
    long addUser(UserAddRequest userAddRequest, User loginUser);

    /**
     * 获取用户脱敏的VO list
     *
     * @param records 记录
     * @return {@code List<UserVO>}
     */
    List<UserVO> getUserVOList(List<User> records);

    /**
     * 获取用户vo(脱敏)
     *
     * @param user 用户
     * @return {@code UserVO}
     */
    UserVO getUserVO(User user);

    /**
     * 用户密码重置(admin)
     *
     * @param userId 用户id
     * @return {@code Boolean}
     */
    boolean userPasswordResetByAdmin(Long userId);

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           请求
     * @return boolean
     */
    int updateUserInfo(UserUpdateRequest userUpdateRequest, HttpServletRequest request);

    /**
     * 是否为admin
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return boolean
     */
    boolean isAdmin(User user);

    /**
     * 用户通过电子邮件重置pwd
     *
     * @param userResetPwdByEmailStepRequest 用户重置pwd请求
     * @return {@link Boolean }
     */
    int userResetPwdByEmail(UserResetPwdByEmailStepRequest userResetPwdByEmailStepRequest);

    /**
     * 删除用户
     *
     * @param id   id
     * @param user 用户
     */
    int deleteUser(Long id, User user);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 删除用户批处理
     *
     * @param ids  身份证
     * @return {@link List }<{@link Long }>
     */
    List<Long> deleteUserBatch(List<Long> ids);

}
