package com.xiaoyuer.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyuer.springboot.mapper.UserMapper;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
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


    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册 DTO
     * @return long
     */
    @Override
    public long userRegister(UserRegisterDto userRegisterDto) {
        //// 获取参数
        //String userAccount = userRegisterDto.getUserAccount();
        //String userPassword = userRegisterDto.getUserPassword();
        //String checkPassword = userRegisterDto.getCheckPassword();
        //String mail = userRegisterDto.getMail();
        //String checkCode = userRegisterDto.getCheckCode();
        //// 1. 只需手动校验有无传递必填项
        //if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, mail, checkCode)) {
        //    throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        //}
        //UserFormValid userFormValid = new UserFormValid();
        //BeanUtils.copyProperties(userRegisterRequest, userFormValid);
        //validFormValue(userFormValid);
        //
        //synchronized (userAccount.intern()) {
        //    // 账户不能重复
        //    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //    queryWrapper.eq("userAccount", userAccount);
        //    long count = this.baseMapper.selectCount(queryWrapper);
        //    if (count > 0) {
        //        throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        //    }
        //    // 2. 加密
        //    String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //    // 3. 插入数据
        //    User user = new User();
        //    user.setUserAccount(userAccount);
        //    user.setUserPassword(encryptPassword);
        //    user.setEmail(mail);
        //    boolean saveResult = this.save(user);
        //    if (!saveResult) {
        //        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        //    }
        //    return user.getId();
        //}
        return 1L;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return null;
    }
}
