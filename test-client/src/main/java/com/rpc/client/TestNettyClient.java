package com.rpc.client;

import com.rpc.api.HelloObject;
import com.rpc.api.HelloService;
import com.rpc.core.RpcClientProxy;
import com.rpc.core.netty.client.NettyClient;
import com.rpc.core.serializer.JsonSerializer;
import com.rpc.entity.RpcResponse;

public class TestNettyClient {

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9999);
        nettyClient.setSerializer(new JsonSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is netty style");
        String res = helloService.hello(object);
        System.out.println(res);

    }
}
