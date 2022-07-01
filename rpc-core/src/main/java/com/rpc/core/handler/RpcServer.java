package com.rpc.core.handler;

import com.rpc.core.registry.ServiceRegistry;
import com.rpc.core.serializer.CommonSerializer;

public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    int DEFAULT_REGISTRY = ServiceRegistry.NACOS_REGISTER;

    void start();

    <T> void publishService(T service, String serviceName);


}
