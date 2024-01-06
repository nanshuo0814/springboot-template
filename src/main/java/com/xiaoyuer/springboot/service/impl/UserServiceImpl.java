package com.xiaoyuer.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.RedisKeyConstant;
import com.xiaoyuer.springboot.constant.UserConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.exception.ThrowUtils;
import com.xiaoyuer.springboot.mapper.UserMapper;
import com.xiaoyuer.springboot.model.dto.user.*;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserLoginVO;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


/**
 * 用户服务实现
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:30:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册 DTO
     * @return long
     */
    @Override
    public long userRegister(UserRegisterDto userRegisterDto) {
        // 获取参数
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        String checkPassword = userRegisterDto.getCheckPassword();
        String email = userRegisterDto.getEmail();
        String emailCaptcha = userRegisterDto.getEmailCaptcha();
        String imageCaptcha = userRegisterDto.getImageCaptcha();

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
            long userAccountId = this.baseMapper.selectCount(queryWrapper);
            if (userAccountId > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册,请重新输入一个");
            }

            // BCrypt加密
            String encryptPassword = bCryptPasswordEncoder.encode(UserConstant.SALT + userPassword);
            // MD5加密
            //String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

            // 插入数据
            User user = new User();
            user.setUserName(userRegisterDto.getUserName());
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
     * @param userLoginDto 用户登录dto
     * @return {@code UserLoginVO}
     */
    @Override
    public UserLoginVO userLogin(HttpServletRequest request, UserLoginDto userLoginDto) {

        // 获取参数
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();
        String imageCaptcha = userLoginDto.getImageCaptcha();

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

        boolean matches = bCryptPasswordEncoder.matches(UserConstant.SALT + userPassword, user.getUserPassword());
        if (!matches) {
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
     * @param userPasswordUpdateDto 用户密码重置dto
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordUpdate(HttpServletRequest request, UserPasswordUpdateDto userPasswordUpdateDto) {
        // 获取参数
        String oldPassword = userPasswordUpdateDto.getOldPassword();
        String userPassword = userPasswordUpdateDto.getUserPassword();
        String checkPassword = userPasswordUpdateDto.getCheckPassword();
        String email = userPasswordUpdateDto.getEmail();
        String emailCaptcha = userPasswordUpdateDto.getEmailCaptcha();

        // 获取当前用户
        User loginUser = this.getById(this.getLoginUser(request).getUserId());

        // 判断旧密码是否正确
        boolean matches = bCryptPasswordEncoder.matches(UserConstant.SALT + oldPassword, loginUser.getUserPassword());
        if (!matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确旧密码");
        }

        // 判断两次密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次新密码输入不一致");
        }

        // 验证邮箱
        if (!loginUser.getUserEmail().equals(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入当前用户的邮箱");
        }

        // 验证邮箱验证码
        Object trueEmailCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.EMAIL_CAPTCHA_KEY + email);
        if (ObjectUtils.isEmpty(trueEmailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
        }
        if (!emailCaptcha.equals(trueEmailCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的邮箱验证码");
        }

        // 修改密码
        String encryptPassword = bCryptPasswordEncoder.encode(UserConstant.SALT + userPassword);
        User user = new User();
        user.setUserId(loginUser.getUserId());
        user.setUserPassword(encryptPassword);
        // 更新用户密码
        return this.updateById(user);
    }

    /**
     * 用户密码重置
     *
     * @param request              请求
     * @param userPasswordResetDto 用户密码重置dto
     * @return {@code Boolean}
     */
    @Override
    public Boolean userPasswordReset(HttpServletRequest request, UserPasswordResetDto userPasswordResetDto) {
        // 获取参数
        String userAccount = userPasswordResetDto.getUserAccount();
        String userPassword = userPasswordResetDto.getUserPassword();
        String checkPassword = userPasswordResetDto.getCheckPassword();
        String email = userPasswordResetDto.getEmail();
        String emailCaptcha = userPasswordResetDto.getEmailCaptcha();

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
        Object trueEmailCaptcha = redisTemplate.opsForValue().get(RedisKeyConstant.EMAIL_CAPTCHA_KEY + email);
        if (ObjectUtils.isEmpty(trueEmailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
        }
        if (!emailCaptcha.equals(trueEmailCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确的邮箱验证码");
        }

        // 修改密码
        synchronized (userAccount.intern()) {
            // 加密密码
            String encryptPassword = bCryptPasswordEncoder.encode(UserConstant.SALT + userPassword);
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
     * @param userAddDto 用户添加dto
     * @return {@code Long}
     */
    @Override
    public Long addUser(UserAddDto userAddDto) {
        // 判断参数（不是必须的）,设置默认值
        if (StringUtils.isEmpty(userAddDto.getUserName())) {
            // 设置默认的用户名（UserConstant.DEFAULT_USER_NAME+当时的时间戳）
            userAddDto.setUserName(UserConstant.DEFAULT_USER_NAME + System.currentTimeMillis());
        }
        if (StringUtils.isEmpty(userAddDto.getUserPassword())) {
            // 设置默认的密码（UserConstant.DEFAULT_USER_PASSWORD）
            userAddDto.setUserPassword(UserConstant.DEFAULT_USER_PASSWORD);
        }
        if (StringUtils.isEmpty(userAddDto.getUserProfile())) {
            // 设置默认的简介（UserConstant.DEFAULT_USER_PROFILE）
            userAddDto.setUserProfile(UserConstant.DEFAULT_USER_PROFILE);
        }
        if (StringUtils.isEmpty(userAddDto.getUserAvatar())) {
            // 设置默认的头像（UserConstant.DEFAULT_USER_AVATAR）
            userAddDto.setUserAvatar(UserConstant.DEFAULT_USER_AVATAR);
        }
        if (StringUtils.isEmpty(userAddDto.getUserRole())) {
            // 设置默认的角色（UserConstant.DEFAULT_ROLE）
            userAddDto.setUserRole(UserConstant.DEFAULT_ROLE);
        }
        if (userAddDto.getUserGender() == null || userAddDto.getUserGender() < 0 || userAddDto.getUserGender() > 2) {
            // 设置默认的性别（UserConstant.DEFAULT_USER_GENDER）
            userAddDto.setUserGender(UserConstant.DEFAULT_USER_GENDER);
        }

        // 校验用户账号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAddDto.getUserAccount());
        User user = this.baseMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号已存在,请换一个");
        }

        // 校验用户邮箱是否存在
        if (!StringUtils.isEmpty(userAddDto.getUserEmail())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserEmail, userAddDto.getUserEmail());
            user = this.baseMapper.selectOne(queryWrapper);
            if (user != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户邮箱已存在,请换一个");
            }
        }

        // 加密密码
        String encryptPassword = bCryptPasswordEncoder.encode(UserConstant.SALT + userAddDto.getUserPassword());
        userAddDto.setUserPassword(encryptPassword);

        // 保存用户
        User userEntity = new User();
        BeanUtils.copyProperties(userAddDto, userEntity);
        boolean result = this.save(userEntity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "未知错误,添加用户失败");
        return userEntity.getUserId();
    }

}
