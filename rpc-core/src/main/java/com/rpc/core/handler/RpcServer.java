package com.rpc.core.handler;

public interface RpcServer {

    <T> void publishService(T service, String serviceName);


}
