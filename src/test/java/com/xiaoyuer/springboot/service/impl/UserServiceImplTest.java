package com.xiaoyuer.springboot.service.impl;


import com.xiaoyuer.springboot.constant.UserConstant;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * 用户服务实施测试
 *
 * @author 小鱼儿
 * @date 2024/01/03 20:03:26
 */
class UserServiceImplTest {

    /**
     * md5加密
     */
    @Test
    void testMd5() {
        String password = "777777"; // xiaoyuer
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + password).getBytes());
        System.out.println(encryptPassword); // 92af571afde0a637bb3401ec95e7f92c
    }

    @Test
    void testTime(){
        // 获取当前时间戳（毫秒级）
        long timestamp = System.currentTimeMillis();

        // 将时间戳转换为日期对象
        Date dateObject = new Date(timestamp);
        System.out.println(dateObject);

        // 将日期对象转换为时间戳
        long newTimestamp = dateObject.getTime();
        System.out.println(newTimestamp);
    }

}