package com.nanshuo.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.PageConstant;
import com.nanshuo.springboot.constant.RedisKeyConstant;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.mapper.UserMapper;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.user.*;
import com.nanshuo.springboot.model.vo.user.UserLoginVO;
import com.nanshuo.springboot.model.vo.user.UserSafetyVO;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    private final UserMapper userMapper;

    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate, UserMapper userMapper) {
        this.redisTemplate = redisTemplate;
        this.userMapper = userMapper;
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册 Request
     * @return long
     */
    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
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
     * 获取当前登录用户
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
        redisTemplate.opsForValue().set(cacheKey, user, UserConstant.USER_CACHE_TIME_OUT, TimeUnit.HOURS);
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
     * 用户密码自行更新
     *
     * @param request               请求
     * @param userPasswordUpdateRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordUpdateByMyself(HttpServletRequest request, UserPasswordUpdateRequest userPasswordUpdateRequest) {
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
     * 用户密码重置通过邮箱重置
     *
     * @param request              请求
     * @param userPasswordResetRequest 用户密码重置Request
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordResetByEmail(HttpServletRequest request, UserPasswordResetRequest userPasswordResetRequest) {
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

    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加Request
     * @return {@code Long}
     */
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        // 判断参数（不是必须的）,设置默认值
        if (StringUtils.isEmpty(userAddRequest.getUserName())) {
            // 设置默认的用户名（UserConstant.DEFAULT_USER_NAME+当时的时间戳）
            userAddRequest.setUserName(UserConstant.DEFAULT_USER_NAME + System.currentTimeMillis());
        }
        if (StringUtils.isEmpty(userAddRequest.getUserPassword())) {
            // 设置默认的密码（UserConstant.DEFAULT_USER_PASSWORD）
            userAddRequest.setUserPassword(UserConstant.DEFAULT_USER_PASSWORD);
        }
        if (StringUtils.isEmpty(userAddRequest.getUserProfile())) {
            // 设置默认的简介（UserConstant.DEFAULT_USER_PROFILE）
            userAddRequest.setUserProfile(UserConstant.DEFAULT_USER_PROFILE);
        }
        if (StringUtils.isEmpty(userAddRequest.getUserAvatar())) {
            // 设置默认的头像（UserConstant.DEFAULT_USER_AVATAR）
            userAddRequest.setUserAvatar(UserConstant.DEFAULT_USER_AVATAR);
        }
        if (StringUtils.isEmpty(userAddRequest.getUserRole())) {
            // 设置默认的角色（UserConstant.DEFAULT_ROLE）
            userAddRequest.setUserRole(UserConstant.USER_ROLE);
        }
        if (userAddRequest.getUserGender() == null || userAddRequest.getUserGender() < 0 || userAddRequest.getUserGender() > 2) {
            // 设置默认的性别（UserConstant.DEFAULT_USER_GENDER）
            userAddRequest.setUserGender(UserConstant.DEFAULT_USER_GENDER);
        }

        // 校验用户账号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAddRequest.getUserAccount());
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户账号已存在,请换一个");
        }

        // 校验用户邮箱是否存在
        if (!StringUtils.isEmpty(userAddRequest.getUserEmail())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserEmail, userAddRequest.getUserEmail());
            user = this.baseMapper.selectOne(queryWrapper);
            if (user != null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户邮箱已存在,请换一个");
            }
        }

        // 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userAddRequest.getUserPassword()).getBytes());
        userAddRequest.setUserPassword(encryptPassword);

        // 保存用户
        User userEntity = new User();
        BeanUtils.copyProperties(userAddRequest, userEntity);
        boolean result = this.save(userEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "未知错误,添加用户失败");
        return userEntity.getUserId();
    }

    /**
     * 获取用户脱敏 vo
     *
     * @param user 用户
     * @return {@code UserVO}
     */
    @Override
    public UserSafetyVO getUserSafeVO(User user) {
        if (user == null) {
            return null;
        }
        UserSafetyVO userSafetyVO = new UserSafetyVO();
        BeanUtils.copyProperties(user, userSafetyVO);
        return userSafetyVO;
    }

    /**
     * 获取用户脱敏 vo list
     *
     * @param userList 用户列表
     * @return {@code List<UserSafetyVO>}
     */
    @Override
    public List<UserSafetyVO> getUserSafeVOList(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserSafeVO).collect(Collectors.toList());
    }

    /**
     * 用户密码由admin重置
     *
     * @param userId 用户id
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordResetByAdmin(Long userId) {
        // 根据id获取用户
        User user = this.getById(userId);
        // 用户存在
        if (user != null) {
            String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + UserConstant.DEFAULT_USER_PASSWORD).getBytes());
            user.setUserPassword(encryptPassword);
            return this.updateById(user);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
    }

    /**
     * 更新用户信息
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           请求
     * @return {@code Integer}
     */
    @Override
    public Integer updateUserInfo(UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        // 获取要修改update更新的用户ID
        long updateUserId = userUpdateRequest.getUserId();
        // 判断用户id是否合法
        if (updateUserId < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前的登录用户信息
        User loginUser = this.getLoginUser(request);
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE) && updateUserId != loginUser.getUserId()) {
            // 即不是管理员也不是当前登录的用户，无权限修改update用户信息，抛异常
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 通过用户id来查询要update的用户信息，更新前原始用户数据
        User oldUser = userMapper.selectById(updateUserId);
        if (oldUser == null) {
            // 用户不存在，抛异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在！");
        }
        // new一个空用户对象
        User updateUser = new User();
        // 判断传进来的各个参数是否为空，不为空则更新
        updateUser.setUserId(oldUser.getUserId());
        updateUser.setUserAccount(!ObjectUtils.isEmpty(userUpdateRequest.getUserAccount()) ? userUpdateRequest.getUserAccount() : oldUser.getUserAccount());
        updateUser.setUserName(!ObjectUtils.isEmpty(userUpdateRequest.getUserName()) ? userUpdateRequest.getUserName() : oldUser.getUserName());
        updateUser.setUserAvatar(!ObjectUtils.isEmpty(userUpdateRequest.getUserAvatar()) ? userUpdateRequest.getUserAvatar() : oldUser.getUserAvatar());
        updateUser.setUserEmail(!ObjectUtils.isEmpty(userUpdateRequest.getUserEmail()) ? userUpdateRequest.getUserEmail() : oldUser.getUserEmail());
        updateUser.setUserGender(!ObjectUtils.isEmpty(userUpdateRequest.getUserGender()) ? userUpdateRequest.getUserGender() : oldUser.getUserGender());
        updateUser.setUserProfile(!ObjectUtils.isEmpty(userUpdateRequest.getUserProfile()) ? userUpdateRequest.getUserProfile() : oldUser.getUserProfile());
        // 返回更新结果
        return userMapper.updateById(updateUser);
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

}