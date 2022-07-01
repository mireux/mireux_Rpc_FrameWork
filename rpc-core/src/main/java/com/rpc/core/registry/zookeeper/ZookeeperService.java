package com.rpc.core.registry.zookeeper;

import com.rpc.core.balancer.LoadBalancer;
import com.rpc.core.registry.LookUpService;
import com.rpc.core.registry.NacosService;
import com.rpc.core.registry.ServiceRegistry;
import com.rpc.utils.ConfigUtil;
import com.rpc.utils.SerializeUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Properties;

public class ZookeeperService implements LookUpService, ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(NacosService.class);

    private static final CuratorFramework curatorFramework;

    static {
        Properties propertiesConfig = ConfigUtil.getPropertiesConfig(null);
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(propertiesConfig.get("zookeeper.host") + ":" + propertiesConfig.get("zookeeper.port")).  // 若配置集群则是 ip1:port, ip2:post, ip3:port(,隔开)
                        sessionTimeoutMs(5000).  // 超时，也是心跳时间
                        retryPolicy(new ExponentialBackoffRetry(1000, 3))// 重试策略，3次且每次多1s
                .build();
        curatorFramework.start();
    }

    private final LoadBalancer loadBalancer;

    public ZookeeperService(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public InetSocketAddress getService(String serviceName) {
        ZookeeperInstance deserialize;
        try {
            byte[] bytes = curatorFramework.getData().forPath("/mireux/providers/" + serviceName);
            deserialize = (ZookeeperInstance) SerializeUtils.deserialize(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new InetSocketAddress(deserialize.getHost(), deserialize.getPort());
    }

    /**
     * 使用zookeeper注册
     *
     * @param serviceName       待注册的服务实体
     * @param inetSocketAddress
     * @param <T>
     */
    @Override
    public <T> void register(String serviceName, InetSocketAddress inetSocketAddress) {
        // 注册就是将ip 地址 端口 服务名 写入到zookeeper
        try {
            ZookeeperInstance zookeeperInstance = new ZookeeperInstance();
            zookeeperInstance.setPort(inetSocketAddress.getPort());
            zookeeperInstance.setHost(inetSocketAddress.getHostString());
            zookeeperInstance.setServiceName(serviceName);
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath("/mireux/providers/" + serviceName, SerializeUtils.serialize(zookeeperInstance));
            getService(serviceName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
