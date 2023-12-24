package com.xiaoyuer.springboot.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.model.entity.User;

/**
 * 用户服务
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:29:48
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册信息
     * @return 注册成功的用户id
     */
    long userRegister(UserRegisterDto userRegisterDto);
}
