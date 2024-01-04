package com.xiaoyuer.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.UserConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.mapper.UserMapper;
import com.xiaoyuer.springboot.model.dto.user.UserLoginDto;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.model.vo.user.UserLoginVO;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


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
        String emailCode = userRegisterDto.getEmailCode();
        String imageCode = userRegisterDto.getImageCode();

        // 确认密码校验
        if (checkPassword != null && !checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 校验邮箱验证码,在邮箱不为null的情况下校验
        if (emailCode != null && email != null) {
            //String code = (String) redisTemplate.opsForValue().get(RedisKeyConstant.EMAIL_CODE_KEY + email);
            // TODO：测试阶段,邮箱验证码不校验或给定固定值
            String code = "111111";
            if (code == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码已过期,请重新获取");
            }
            if (!emailCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱验证码错误");
            }
        }

        // 邮箱校验
        if (email != null) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserEmail, email);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册,请重新输入一个");
            }
        }

        // 图片验证码校验
        if (imageCode != null) {
            //String code = (String) redisTemplate.opsForValue().get(RedisKeyConstant.IMAGE_CODE_KEY + imageCode);
            // TODO：测试阶段,图片验证码不校验或给定固定值
            String code = "111111";
            if (code == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码已过期,请重新获取");
            }
            if (!imageCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
            }
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
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
        String imageCode = userLoginDto.getImageCode();

        // 校验图片验证码
        if (imageCode != null) {
            //String code = (String) redisTemplate.opsForValue().get(RedisKeyConstant.IMAGE_CODE_KEY + userLoginDto.getImageCode());
            // TODO：测试阶段,图片验证码不校验或给定固定值
            String code = "111111";
            if (code == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码已过期,请重新获取");
            }
            if (!imageCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片验证码错误");
            }
        }

        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User user = this.baseMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "账号或密码错误");
        }

        boolean matches = bCryptPasswordEncoder.matches(UserConstant.SALT + userPassword, user.getUserPassword());
        if (!matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 记录用户的登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);

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
        return null;
    }
}
