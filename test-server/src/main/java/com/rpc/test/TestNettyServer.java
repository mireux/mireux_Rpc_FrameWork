package com.rpc.test;

import com.rpc.api.HelloService;
import com.rpc.core.netty.server.NettyServer;
import com.rpc.core.serializer.JsonSerializer;

public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.setSerializer(new JsonSerializer());
        server.publishService(helloService,HelloService.class);
    }
}
