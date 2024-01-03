package com.xiaoyuer.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyuer.springboot.common.ErrorCode;
import com.xiaoyuer.springboot.constant.NumberConstant;
import com.xiaoyuer.springboot.constant.RedisKeyConstant;
import com.xiaoyuer.springboot.constant.UserConstant;
import com.xiaoyuer.springboot.exception.BusinessException;
import com.xiaoyuer.springboot.mapper.UserMapper;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
    private RedisTemplate<String, String> redisTemplate;


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
        String mail = userRegisterDto.getEmail();
        String checkCode = userRegisterDto.getCheckCode();

        // 确认密码校验
        if (checkPassword != null && !checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 校验验证码,在邮箱不为null的情况下校验
        if (checkCode != null && mail != null) {
            String code = redisTemplate.opsForValue().get(RedisKeyConstant.YZM_PRE + mail);
            if (code == null || code.length() != NumberConstant.EMAIL_CODE_LENGTH) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新获取验证码");
            }
            if (!checkCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
        }

        // 邮箱校验
        if (mail != null) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", mail);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该邮箱已被注册,请重新输入一个");
            }
        }

        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册,请重新输入一个");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setEmail(mail);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，系统内部错误");
            }
            return user.getId();
        }
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return null;
    }
}
