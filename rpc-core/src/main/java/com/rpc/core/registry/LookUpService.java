package com.rpc.core.registry;

import com.rpc.core.balancer.RoundRobinLoadBalancer;
import com.rpc.core.registry.zookeeper.ZookeeperService;

import java.net.InetSocketAddress;

public interface LookUpService {

    /**
     * 根据用户名获取对应的具体服务
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress getService(String serviceName);

    static LookUpService getRegistry(Integer code) {
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
