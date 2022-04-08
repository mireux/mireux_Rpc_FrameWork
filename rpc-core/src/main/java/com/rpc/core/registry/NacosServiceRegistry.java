package com.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry{

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";
    private static final NamingService namingService;
    static {
        try {
            //连接Nacos创建命名服务
            namingService = NamingFactory.createNamingService(SERVER_ADDRESS);
        }catch (NacosException e){
            logger.error("连接Nacos时有错误发生：" + e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }


    @Override
    public  void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            logger.info("register:{}",serviceName);
            namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生" + e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress getService(String serviceName) {
        try {
            List<Instance> AllService = namingService.getAllInstances(serviceName);
            //TODO 这里涉及到负载均衡策略，后面再优化，这里我们先选择第0个
            Instance instance = AllService.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生" + e);
        }
        return null;
    }
}
