package com.rpc.client;

import com.rpc.api.HelloService;
import com.rpc.api.TestService;
import com.rpc.api.entity.HelloObject;
import com.rpc.core.handler.RpcClientProxy;
import com.rpc.core.netty.client.NettyClient;
import com.rpc.core.serializer.JsonSerializer;

public class TestNettyClient {

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        nettyClient.setSerializer(new JsonSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is netty style");
        String res = helloService.hello(object);
        System.out.println(res);
        TestService testService = rpcClientProxy.getProxy(TestService.class);
        res = testService.test("这是netty提供的服务");
        System.out.println("res = " + res);
    }
}
