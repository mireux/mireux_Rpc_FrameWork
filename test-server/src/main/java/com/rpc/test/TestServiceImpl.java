package com.rpc.test;


import com.rpc.annotation.AutoRegisterService;
import com.rpc.api.TestService;

@AutoRegisterService
public class TestServiceImpl implements TestService {
    @Override
    public String test(String msg) {
        return msg;
    }
}
