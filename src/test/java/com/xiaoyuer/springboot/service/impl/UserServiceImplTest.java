package com.xiaoyuer.springboot.service.impl;


import com.xiaoyuer.springboot.constant.UserConstant;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实施测试
 *
 * @author 小鱼儿
 * @date 2024/01/03 20:03:26
 */
class UserServiceImplTest {


    /**
     * 测试bcrypt
     */
    @Test
    void testBCrypt() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "xiaoyuer"; // xiaoyuer
        String encryptPassword = passwordEncoder.encode(UserConstant.SALT + password);
        System.out.println(encryptPassword); // $2a$10$RsWDM.4Fo2z8uvjG89chA.I2vRhxKWHr3Km431E4BM9kGCOQSb9E2
    }

    /**
     * md5加密
     */
    @Test
    void testMd5() {
        String password = "xiaoyuer"; // xiaoyuer
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + password).getBytes());
        System.out.println(encryptPassword); // 92af571afde0a637bb3401ec95e7f92c
    }

}