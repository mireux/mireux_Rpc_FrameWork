package com.rpc.core;

public interface RpcServer {

    <T> void publishService(Object service, Class<T> serviceClass);

    void start(int port);
}
