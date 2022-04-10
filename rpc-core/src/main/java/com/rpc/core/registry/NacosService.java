package com.rpc.core.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.rpc.core.balancer.LoadBalancer;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.utils.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosService implements ServiceRegistry, LookUpService {

    private static final Logger logger = LoggerFactory.getLogger(NacosService.class);

    private static NamingService namingService;

    private final LoadBalancer loadBalancer;


    public NacosService(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
        namingService = NacosUtil.getNacosNamingService();
    }


    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            logger.info("register:{}", serviceName);
            NacosUtil.registerService(namingService, serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生" + e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress getService(String serviceName) {
        try {
            List<Instance> AllService = NacosUtil.getAllInstance(namingService, serviceName);
//            Instance instance = AllService.get(0);
            if (AllService.size() == 0) {
                logger.error("找不到对应服务：" + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(AllService);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生" + e);
        }
        return null;
    }
}
