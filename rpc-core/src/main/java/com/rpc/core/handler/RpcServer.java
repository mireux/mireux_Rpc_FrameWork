package com.rpc.core.handler;

public interface RpcServer {

    <T> void publishService(T service, Class<T> serviceClass);

    void start(int port);
}
