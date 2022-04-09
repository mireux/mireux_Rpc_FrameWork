package com.rpc.core.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册通用接口
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     * @param  serviceName 待注册的服务实体
     * @param <T> 服务实体类
     */
    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);

}
