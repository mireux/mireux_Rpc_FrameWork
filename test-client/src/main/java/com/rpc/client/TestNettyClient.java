package com.rpc.client;

import com.rpc.api.HelloService;
import com.rpc.api.TestService;
import com.rpc.api.entity.HelloObject;
import com.rpc.core.handler.RpcClientProxy;
import com.rpc.core.netty.client.NettyClient;
import com.rpc.core.serializer.JsonSerializer;
import com.rpc.utils.ConfigUtil;

import java.util.Properties;

import static com.rpc.core.registry.ServiceRegistry.NACOS_REGISTER;
import static com.rpc.core.registry.ServiceRegistry.ZOOKEEPER_REGISTER;

public class TestNettyClient {

    public static void main(String[] args) {
        Integer register = null;
        Properties propertiesConfig = ConfigUtil.getPropertiesConfig(TestNettyClient.class);
        String registry = (String) propertiesConfig.get("registry");
        if (registry.equals("nacos")) {
            register = NACOS_REGISTER;
        } else if (registry.equals("zookeeper")) {
            register = ZOOKEEPER_REGISTER;
        }
        NettyClient nettyClient = new NettyClient(0, register);
        nettyClient.setSerializer(new JsonSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(nettyClient);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is netty style");
        String res = helloService.hello(object);
        System.out.println(res);
        TestService testService = rpcClientProxy.getProxy(TestService.class);
        testService.test("这是netty提供的服务");
    }
}
