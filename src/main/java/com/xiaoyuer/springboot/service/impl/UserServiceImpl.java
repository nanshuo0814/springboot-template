package com.xiaoyuer.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaoyuer.springboot.mapper.UserMapper;
import com.xiaoyuer.springboot.model.entity.User;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:30:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
