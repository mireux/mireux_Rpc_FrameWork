package com.rpc.test;

import com.rpc.api.HelloService;
import com.rpc.core.netty.server.NettyServer;
import com.rpc.core.registry.DefaultServiceRegistry;
import com.rpc.core.registry.ServiceRegistry;

public class TestNettyServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();
        defaultServiceRegistry.register(helloService);
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(9999);
    }
}
