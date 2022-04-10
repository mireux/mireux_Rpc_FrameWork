package com.rpc.test;

import com.rpc.annotation.AutoRegisterServiceScan;
import com.rpc.core.netty.server.NettyServer;

@AutoRegisterServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}
