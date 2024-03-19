package com.nanshuo.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.PageConstant;
import com.nanshuo.springboot.constant.RedisKeyConstant;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.exception.ThrowUtils;
import com.nanshuo.springboot.mapper.UserMapper;
import com.nanshuo.springboot.model.request.user.*;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.request.user.admin.AdminAddUserRequest;
import com.nanshuo.springboot.model.vo.user.UserLoginVO;
import com.nanshuo.springboot.model.vo.user.UserSafetyVO;
import com.nanshuo.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * 用户服务实现
 *
 * @author nanshuo
 * @date 2023/12/23 16:30:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;

    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册 Request
     * @return long
     */
    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 获取参数
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String emailCaptcha = userRegisterRequest.getEmailCaptcha();
        String imageCaptcha = userRegisterRequest.getImageCaptcha();

        // 确认密码校验
        if (checkPassword != null && !checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 校验邮箱验证码,在邮箱不为null的情况下校验
        Object trueEmailCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.EMAIL_CAPTCHA_KEY + email);
        if (ObjectUtils.isEmpty(trueEmailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
        }
        if (!emailCaptcha.equals(trueEmailCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码错误");
        }

        // 邮箱校验
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserEmail, email);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册,请重新输入一个");
        }

        // 图片验证码校验
        Object trueImageCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.IMAGE_CAPTCHA_KEY);
        if (ObjectUtils.isEmpty(trueImageCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码已过期,请重新获取");
        }
        if (!imageCaptcha.equals(trueImageCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getUserAccount, userAccount);
            long userAccountId = this.baseMapper.selectCount(qw);
            if (userAccountId > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册,请重新输入一个");
            }

            // MD5加密
            String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

            // 插入数据
            User user = new User();
            user.setUserName(userRegisterRequest.getUserName());
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserEmail(email);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，系统内部错误");
            }
            return user.getUserId();
        }
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录Request
     * @return {@code UserLoginVO}
     */
    @Override
    public UserLoginVO userLogin(HttpServletRequest request, UserLoginRequest userLoginRequest) {

        // 获取参数
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String imageCaptcha = userLoginRequest.getImageCaptcha();

        // 校验图片验证码
        Object trueImageCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.IMAGE_CAPTCHA_KEY);
        if (ObjectUtils.isEmpty(trueImageCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码已过期,请重新获取");
        }
        if (!imageCaptcha.equals(trueImageCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
        }

        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = this.baseMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "账号或密码错误");
        }

        if (user.getUserRole().equals(UserConstant.BAN_ROLE)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "账号已被禁用,请联系管理员解封");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
        if (!encryptPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        // 缓存用户信息
        redisTemplate.opsForValue().set(RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getUserId(), user);

        // 返回用户登录信息
        return this.getLoginUserVO(user);
    }

    /**
     * 获取登录用户vo
     *
     * @param user 用户
     * @return {@code UserLoginVO}
     */
    @Override
    public UserLoginVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        return userLoginVO;
    }

    /**
     * 获取登录用户
     *
     * @param request 请求
     * @return {@code User}
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {

        // 先判断是否已登录,获取用户信息
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 尝试从缓存redis中通过用户id获取用户信息
        User cachedUser = this.getUserCacheById(user.getUserId());

        if (cachedUser == null) {
            // 缓存中不存在，从数据库查询
            cachedUser = this.getById(user.getUserId());

            if (cachedUser != null) {
                // 将用户信息放入缓存
                this.saveUserToCache(cachedUser);
            } else {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            }
        }
        // 返回用户信息
        return cachedUser;
    }

    /**
     * 按id获取用户缓存
     *
     * @param userId 用户id
     * @return {@code User}
     */
    @Override
    public User getUserCacheById(Long userId) {
        String cacheKey = RedisKeyConstant.USER_LOGIN_STATE_CACHE + userId;
        return (User) redisTemplate.opsForValue().get(cacheKey);
    }

    /**
     * 将用户保存到缓存
     *
     * @param user 用户
     */
    @Override
    public void saveUserToCache(User user) {
        String cacheKey = RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getUserId();
        redisTemplate.opsForValue().set(cacheKey, user, 1, TimeUnit.HOURS);
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code Boolean}
     */
    @Override
    public String userLogout(HttpServletRequest request) {
        // 判断是否已登录
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 删除缓存
        redisTemplate.delete(RedisKeyConstant.USER_LOGIN_STATE_CACHE + this.getLoginUser(request).getUserId());
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return "退出登录成功！";
    }

    /**
     * 用户密码更新
     *
     * @param request               请求
     * @param userPasswordUpdateRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordUpdate(HttpServletRequest request, UserPasswordUpdateRequest userPasswordUpdateRequest) {
        // 获取参数
        String oldPassword = userPasswordUpdateRequest.getOldPassword();
        String userPassword = userPasswordUpdateRequest.getNewPassword();
        String checkPassword = userPasswordUpdateRequest.getCheckPassword();

        // 获取当前用户
        User loginUser = this.getById(this.getLoginUser(request).getUserId());

        // 判断旧密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + oldPassword).getBytes());
        if (!encryptPassword.equals(loginUser.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确旧密码");
        }

        // 判断两次密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次新密码输入不一致");
        }

        // 修改密码
        String newEncryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
        User user = new User();
        user.setUserId(loginUser.getUserId());
        user.setUserPassword(newEncryptPassword);
        // 更新用户密码
        return this.updateById(user);
    }

    /**
     * 验证电子邮件代码
     *
     * @param email        电子邮件
     * @param emailCaptcha 电子邮件验证码
     */
    private void validateEmailCode(String email, String emailCaptcha) {
        Object trueEmailCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.EMAIL_CAPTCHA_KEY + email);
        if (ObjectUtils.isEmpty(trueEmailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
        }
        if (!emailCaptcha.equals(trueEmailCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的邮箱验证码");
        }
    }

    /**
     * 用户密码重置
     *
     * @param request              请求
     * @param userPasswordResetRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordReset(HttpServletRequest request, UserPasswordResetRequest userPasswordResetRequest) {
        // 获取参数
        String userAccount = userPasswordResetRequest.getUserAccount();
        String userPassword = userPasswordResetRequest.getUserPassword();
        String checkPassword = userPasswordResetRequest.getCheckPassword();
        String email = userPasswordResetRequest.getUserEmail();
        String emailCaptcha = userPasswordResetRequest.getEmailCaptcha();

        // 校验两次密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次新密码输入不一致");
        }

        // 验证账号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户账号不存在");
        }

        // 验证用户是否被禁用
        if (user.getUserRole().equals(UserConstant.BAN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "该用户已被禁用,请联系管理员解封");
        }

        // 验证邮箱
        if (!user.getUserEmail().equals(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不匹配当前用户,请输入当前用户的邮箱");
        }

        // 验证邮箱验证码
        validateEmailCode(email, emailCaptcha);

        // 修改密码
        synchronized (userAccount.intern()) {
            // 加密密码
            String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

            // 更新用户密码
            user.setUserPassword(encryptPassword);
            return this.updateById(user);
        }
    }

    // end domain 用户登录相关

    // domain 用户增删改查相关

    /**
     * 添加用户
     *
     * @param adminAddUserRequest 用户添加Request
     * @return {@code Long}
     */
    @Override
    public Long addUser(AdminAddUserRequest adminAddUserRequest) {
        // 判断参数（不是必须的）,设置默认值
        if (StringUtils.isEmpty(adminAddUserRequest.getUserName())) {
            // 设置默认的用户名（UserConstant.DEFAULT_USER_NAME+当时的时间戳）
            adminAddUserRequest.setUserName(UserConstant.DEFAULT_USER_NAME + System.currentTimeMillis());
        }
        if (StringUtils.isEmpty(adminAddUserRequest.getUserPassword())) {
            // 设置默认的密码（UserConstant.DEFAULT_USER_PASSWORD）
            adminAddUserRequest.setUserPassword(UserConstant.DEFAULT_USER_PASSWORD);
        }
        if (StringUtils.isEmpty(adminAddUserRequest.getUserProfile())) {
            // 设置默认的简介（UserConstant.DEFAULT_USER_PROFILE）
            adminAddUserRequest.setUserProfile(UserConstant.DEFAULT_USER_PROFILE);
        }
        if (StringUtils.isEmpty(adminAddUserRequest.getUserAvatar())) {
            // 设置默认的头像（UserConstant.DEFAULT_USER_AVATAR）
            adminAddUserRequest.setUserAvatar(UserConstant.DEFAULT_USER_AVATAR);
        }
        if (StringUtils.isEmpty(adminAddUserRequest.getUserRole())) {
            // 设置默认的角色（UserConstant.DEFAULT_ROLE）
            adminAddUserRequest.setUserRole(UserConstant.USER_ROLE);
        }
        if (adminAddUserRequest.getUserGender() == null || adminAddUserRequest.getUserGender() < 0 || adminAddUserRequest.getUserGender() > 2) {
            // 设置默认的性别（UserConstant.DEFAULT_USER_GENDER）
            adminAddUserRequest.setUserGender(UserConstant.DEFAULT_USER_GENDER);
        }

        // 校验用户账号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, adminAddUserRequest.getUserAccount());
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在,请换一个");
        }

        // 校验用户邮箱是否存在
        if (!StringUtils.isEmpty(adminAddUserRequest.getUserEmail())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserEmail, adminAddUserRequest.getUserEmail());
            user = this.baseMapper.selectOne(queryWrapper);
            if (user != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户邮箱已存在,请换一个");
            }
        }

        // 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + adminAddUserRequest.getUserPassword()).getBytes());
        adminAddUserRequest.setUserPassword(encryptPassword);

        // 保存用户
        User userEntity = new User();
        BeanUtils.copyProperties(adminAddUserRequest, userEntity);
        boolean result = this.save(userEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "未知错误,添加用户失败");
        return userEntity.getUserId();
    }

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 用户查询Request
     * @return {@code LambdaQueryWrapper<User>}
     */
    @Override
    public LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 获取参数
        Long id = userQueryRequest.getUserId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        Integer gender = userQueryRequest.getUserGender();
        String email = userQueryRequest.getUserEmail();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, User::getUserId, id)
                .eq(gender != null, User::getUserGender, gender)
                .eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole)
                .like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .like(StringUtils.isNotBlank(email), User::getUserEmail, email)
                .like(StringUtils.isNotBlank(userName), User::getUserName, userName)
                .orderBy(StringUtils.isNotEmpty(sortField),
                        sortOrder.equals(PageConstant.SORT_ORDER_ASC), getSortColumn(sortField));
        return lambdaQueryWrapper;
    }

    /**
     * 获取排序列
     *
     * @param sortField 排序字段
     * @return {@code SFunction<User, ?>}
     */
    private SFunction<User, ?> getSortColumn(String sortField) {
        switch (sortField) {
            case PageConstant.USER_ACCOUNT_SORT_FIELD:
                return User::getUserAccount;
            case PageConstant.USER_ID_SORT_FIELD:
                return User::getUserId;
            case PageConstant.USER_NAME_SORT_FIELD:
                return User::getUserName;
            case PageConstant.USER_EMAIL_SORT_FIELD:
                return User::getUserEmail;
            case PageConstant.USER_GENDER_SORT_FIELD:
                return User::getUserGender;
            case PageConstant.USER_ROLE_SORT_FIELD:
                return User::getUserRole;
            case PageConstant.CREATE_TIME_SORT_FIELD:
                return User::getCreateTime;
            case PageConstant.UPDATE_TIME_SORT_FIELD:
                return User::getUpdateTime;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未知排序字段");
    }

    /**
     * 获取用户vo
     *
     * @param user 用户
     * @return {@code UserVO}
     */
    @Override
    public UserSafetyVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserSafetyVO userSafetyVO = new UserSafetyVO();
        BeanUtils.copyProperties(user, userSafetyVO);
        return userSafetyVO;
    }

    /**
     * 获取用户vo
     *
     * @param userList 用户列表
     * @return {@code List<UserVO>}
     */
    @Override
    public List<UserSafetyVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 用户密码由admin重置
     *
     * @param userId 用户id
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordResetByAdmin(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + UserConstant.DEFAULT_USER_PASSWORD).getBytes());
            user.setUserPassword(encryptPassword);
            this.updateById(user);
            return true;
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
    }
}