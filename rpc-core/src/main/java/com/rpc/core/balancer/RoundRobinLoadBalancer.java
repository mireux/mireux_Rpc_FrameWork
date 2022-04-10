package com.rpc.core.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮询算法
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private static int index = 0;


    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index);
    }
}
