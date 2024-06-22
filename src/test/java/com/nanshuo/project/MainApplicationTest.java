package com.nanshuo.project;

import com.nanshuo.project.constant.UserConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Slf4j
@SpringBootTest
class MainApplicationTest {

    /**
     * md5加密
     */
    @Test
    void testMd5() {
        String password = "user123";
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + password).getBytes());
        System.out.println(encryptPassword);
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