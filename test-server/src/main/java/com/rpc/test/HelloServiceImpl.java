package com.rpc.test;

import com.rpc.annotation.AutoRegisterService;
import com.rpc.api.HelloService;
import com.rpc.api.entity.HelloObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoRegisterService
public class HelloServiceImpl implements HelloService {
    /**
     * 使用HelloServiceImpl初始化日志对象，方便在日志输出的时候，可以打印出日志信息所属的类。
     */
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "成功调用hello()方法";
    }
}
