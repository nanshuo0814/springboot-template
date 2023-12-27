package com.xiaoyuer.springboot.controller;

import com.xiaoyuer.springboot.annotation.CheckParam;
import lombok.Data;

@Data
public class TestDto {
    @CheckParam(required = true)
    private String name;
    @CheckParam(required = false, minLength = 6)
    private String password;
    @CheckParam(required = false, minLength = 6)
    private String email;
}
