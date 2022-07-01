package com.rpc.core.registry;

import com.rpc.core.balancer.RoundRobinLoadBalancer;
import com.rpc.core.registry.zookeeper.ZookeeperService;

import java.net.InetSocketAddress;

/**
 * 服务注册通用接口
 */
public interface ServiceRegistry {
    Integer NACOS_REGISTER = 0;
    Integer ZOOKEEPER_REGISTER = 1;

    /**
     * 将一个服务注册进注册表
     *
     * @param serviceName 待注册的服务实体
     * @param <T>         服务实体类
     */
    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);

    static ServiceRegistry getRegistry(Integer code) {
        switch (code) {
            case 0:
                return new NacosService(new RoundRobinLoadBalancer());
            case 1:
                return new ZookeeperService(new RoundRobinLoadBalancer());
            default:
                return null;
        }
    }

}
