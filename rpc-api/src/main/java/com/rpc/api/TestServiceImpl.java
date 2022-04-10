package com.rpc.api;


import com.rpc.annotation.AutoRegisterService;

@AutoRegisterService
public class TestServiceImpl implements TestService {
    @Override
    public String test(String msg) {
        return msg;
    }
}
