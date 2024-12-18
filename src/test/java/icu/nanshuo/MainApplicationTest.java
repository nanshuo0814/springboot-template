package icu.nanshuo;

import icu.nanshuo.constant.UserConstant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

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

}