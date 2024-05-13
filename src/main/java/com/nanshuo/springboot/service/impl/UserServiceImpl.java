package com.nanshuo.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.config.CaptchaConfig;
import com.nanshuo.springboot.constant.PageConstant;
import com.nanshuo.springboot.constant.RedisKeyConstant;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.mapper.UserMapper;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.user.*;
import com.nanshuo.springboot.model.enums.sort.UserSortFieldEnums;
import com.nanshuo.springboot.model.enums.user.UserRoleEnums;
import com.nanshuo.springboot.model.vo.user.UserLoginVO;
import com.nanshuo.springboot.model.vo.user.UserSafetyVO;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.SqlUtils;
import com.nanshuo.springboot.utils.ThrowUtils;
import com.nanshuo.springboot.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nanshuo.springboot.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现
 *
 * @author nanshuo
 * @date 2023/12/23 16:30:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CaptchaConfig captchaConfig;

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
        String captchaKey = userRegisterRequest.getCaptchaKey();

        // 确认密码校验
        if (checkPassword != null && !checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 只有邮箱验证码和它的key不为空且开启了邮箱验证码功能
        if (email != null && emailCaptcha != null && captchaConfig.isEmailEnabled()) {
            // 校验邮箱验证码,在邮箱不为null的情况下校验
            validateEmailCode(email, emailCaptcha);
        }

        // 邮箱校验
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserEmail, email);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册,请重新输入一个");
        }

        // 只有图片验证码和它的key不为空且开启了图片验证码功能
        if (imageCaptcha != null && captchaKey != null && captchaConfig.isImageEnabled()) {
            // 校验图片验证码
            validateImageCaptcha(imageCaptcha, captchaKey);
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
            return user.getId();
        }
    }

    /**
     * 验证图像captcha
     *
     * @param imageCaptcha 图像验证码
     * @param captchaKey   验证码密钥
     */
    private void validateImageCaptcha(String imageCaptcha, String captchaKey) {
        Object trueImageCaptcha = redisUtils.get(RedisKeyConstant.IMAGE_CAPTCHA_KEY + captchaKey);
        if (ObjectUtils.isEmpty(trueImageCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码已过期,请重新获取");
        }
        if (!imageCaptcha.equals(trueImageCaptcha.toString())) {
            // 删除旧的验证码，生成新的验证码
            redisUtils.del(RedisKeyConstant.IMAGE_CAPTCHA_KEY + captchaKey);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
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
        String captchaKey = userLoginRequest.getCaptchaKey();

        // 只有图片验证码和它的key不为空且开启了图片验证码功能
        if (imageCaptcha != null && captchaKey != null && captchaConfig.isImageEnabled()) {
            // 校验图片验证码
            validateImageCaptcha(imageCaptcha, captchaKey);
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
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 缓存用户信息
        redisUtils.set(RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getId(), user);
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
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 尝试从缓存redis中通过用户id获取用户信息
        User cachedUser = this.getUserCacheById(user.getId());
        if (cachedUser == null) {
            // 缓存中不存在，从数据库查询
            cachedUser = this.getById(user.getId());
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
        return (User) redisUtils.get(cacheKey);
    }

    /**
     * 将用户保存到缓存
     *
     * @param user 用户
     */
    @Override
    public void saveUserToCache(User user) {
        String cacheKey = RedisKeyConstant.USER_LOGIN_STATE_CACHE + user.getId();
        redisUtils.set(cacheKey, user, UserConstant.USER_CACHE_TIME_OUT, TimeUnit.HOURS);
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
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 删除缓存
        redisUtils.del(RedisKeyConstant.USER_LOGIN_STATE_CACHE + this.getLoginUser(request).getId());
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return "退出登录成功！";
    }

    /**
     * 用户密码自行更新
     *
     * @param request                   请求
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
        User loginUser = this.getById(this.getLoginUser(request).getId());
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
        user.setId(loginUser.getId());
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
        Object trueEmailCaptcha = redisUtils.get(RedisKeyConstant.EMAIL_CAPTCHA_KEY + email);
        if (ObjectUtils.isEmpty(trueEmailCaptcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
        }
        if (!emailCaptcha.equals(trueEmailCaptcha.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码错误");
        }
    }

    /**
     * 用户密码重置通过邮箱重置
     *
     * @param request                  请求
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
        // 验证邮箱验证码，这里就一定要验证邮箱了
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
        return userEntity.getId();
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
        if (!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE) && updateUserId != loginUser.getId()) {
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
        updateUser.setId(oldUser.getId());
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
     * 是否为admin
     *
     * @param request 请求
     * @return boolean
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    /**
     * 是否为admin
     *
     * @param user 用户
     * @return boolean
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnums.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 用户通过mp open登录
     *
     * @param wxOAuth2UserInfo wx oauth2用户信息
     * @param request          请求
     * @return {@code UserLoginVO}
     */
    @Override
    public UserLoginVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request) {
        String unionId = wxOAuth2UserInfo.getUnionId();
        String mpOpenId = wxOAuth2UserInfo.getOpenid();
        // 单机锁
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User user = this.getOne(queryWrapper);
            // 被封号，禁止登录
            if (user != null && UserRoleEnums.BAN.getValue().equals(user.getUserRole())) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "该用户已被封，禁止登录");
            }
            // 用户不存在则创建
            if (user == null) {
                user = new User();
                user.setUnionId(unionId);
                user.setMpOpenId(mpOpenId);
                user.setUserAvatar(wxOAuth2UserInfo.getHeadImgUrl());
                user.setUserName(wxOAuth2UserInfo.getNickname());
                boolean result = this.save(user);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败");
                }
            }
            // 记录用户的登录态
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
            return getLoginUserVO(user);
        }
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
        lambdaQueryWrapper.eq(id != null, User::getId, id)
                .eq(gender != null, User::getUserGender, gender)
                .eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole)
                .like(StringUtils.isNotBlank(userProfile), User::getUserProfile, userProfile)
                .like(StringUtils.isNotBlank(email), User::getUserEmail, email)
                .like(StringUtils.isNotBlank(userName), User::getUserName, userName)
                .orderBy(SqlUtils.validSortField(sortField),
                        sortOrder.equals(PageConstant.SORT_ORDER_ASC), isSortField(sortField));
        return lambdaQueryWrapper;
    }

    /**
     * 是否为排序字段
     *
     * @param sortField 排序字段
     * @return {@code SFunction<User, ?>}
     */
    private SFunction<User, ?> isSortField(String sortField) {
        // 如果排序字段为空，则默认为更新时间
        if (StringUtils.isBlank(sortField)) {
            sortField = UserSortFieldEnums.UPDATE_TIME.name();
        }
        if (SqlUtils.validSortField(sortField)) {
            return UserSortFieldEnums.fromString(sortField)
                    .map(UserSortFieldEnums::getFieldGetter)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "错误的排序字段"));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段无效");
        }
    }

}