package com.rpc.utils;

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

/**
 * 将nacos注册中心的注册和查找功能分离
 */
public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";

    /**
     * 获取Nacos 创建命名空间
     * @return 创建的服务
     */
    public static NamingService getNacosNamingService() {
        try {
            //连接Nacos创建命名服务
            return NamingFactory.createNamingService(SERVER_ADDRESS);
        } catch (NacosException e) {
            logger.error("连接Nacos时有错误发生：" + e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 注册服务到Nacos
     */
    public static void registerService(NamingService namingService,String serviceName,InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
    }

    /**
     * 根据服务名获取对应的注册到Nacos当中的服务实例
     */
    public static List<Instance> getAllInstance(NamingService namingService,String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }



}
