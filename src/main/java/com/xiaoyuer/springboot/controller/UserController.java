package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.Check;
import com.xiaoyuer.springboot.common.BaseResponse;
import com.xiaoyuer.springboot.common.ResultUtils;
import com.xiaoyuer.springboot.model.dto.user.UserRegisterDto;
import com.xiaoyuer.springboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户控制器
 *
 * @author 小鱼儿
 * @date 2023/12/23 16:33:46
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册 DTO
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/register")
    @Check(checkParam = true)
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        // 检查注册信息是否为空
        //ThrowUtils.throwIfNull(userRegisterDto);
        // 调用用户注册服务方法
        long result = userService.userRegister(userRegisterDto);
        // 返回注册结果
        return ResultUtils.success(result);
    }

}
