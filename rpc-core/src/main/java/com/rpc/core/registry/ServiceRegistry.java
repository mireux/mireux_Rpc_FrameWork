package com.rpc.core.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册通用接口
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     * @param  service 待注册的服务实体
     * @param <T> 服务实体类
     */
    <T> void register(T service);

    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 根据用户名获取对应的具体服务
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress getService(String serviceName);
}
