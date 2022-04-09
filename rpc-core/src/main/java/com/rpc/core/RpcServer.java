package com.rpc.core;

public interface RpcServer {

    <T> void publishService(T service, Class<T> serviceClass);

    void start(int port);
}
