package com.rpc.core.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡
 */
public interface LoadBalancer {

    /**
     * 从一系列的instances中的选择其中一个
     *
     * @param instances
     * @return
     */
    Instance select(List<Instance> instances);


}
