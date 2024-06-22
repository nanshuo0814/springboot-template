package com.nanshuo.project.utils;

import com.nanshuo.project.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class JsonUtilsTest {

    /**
     * 将obj到json（序列化）
     */
    @Test
    void objToJson() {
        String str = "hello";
        Integer i = 1;
        Long l = 2L;
        User user = new User();
        user.setId(1L);
        user.setUserAccount("nanshuo");
        user.setUserName("nanshuo");
        log.info(JsonUtils.objToJson(user));
        log.info(JsonUtils.objToJson(str));
        log.info(JsonUtils.objToJson(i));
        log.info(JsonUtils.objToJson(l));
    }

    /**
     * json到obj（反序列化）
     */
    @Test
    void jsonToObj() {
        String json = "{\"userId\":1,\"userAccount\":\"nanshuo\",\"userName\":\"nanshuo\"}";
        User user = JsonUtils.jsonToObj(json, User.class);
        assert user != null;
        log.info(user.toString());
    }
}