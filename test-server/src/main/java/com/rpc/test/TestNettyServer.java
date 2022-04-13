package com.rpc.test;

import com.alibaba.nacos.api.exception.NacosException;
import com.rpc.annotation.AutoRegisterServiceScan;
import com.rpc.core.netty.server.NettyServer;
import com.rpc.utils.ConfigUtil;

@AutoRegisterServiceScan
public class TestNettyServer {
    public static void main(String[] args) throws NacosException {
        ConfigUtil.getConfigByNacos("localhost", "mireux.rpc.server", "DEFAULT_GROUP");
        String host = ConfigUtil.getConfig("mireux.rpc.server", "server.host");
        System.out.println(host);
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
