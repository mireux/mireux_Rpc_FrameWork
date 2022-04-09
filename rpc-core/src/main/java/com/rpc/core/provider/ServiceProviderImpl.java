package com.rpc.core.provider;


import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    /**
     * key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 用来存放实现类的名称，Set存取更高效，存放实现类名称相比存放接口名称占的空间更小，因为一个实现类可能实现了多个接口，查找效率也会更高
     */
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void addServiceProvider(T service, Class<T> serviceClass) {
        // getCanonicalName获取符合Java规范的名称
        String serviceName = serviceClass.getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口:{} 注册服务:{}", serviceName, serviceName);
    }

    /**
     * 通过接口名获取对应的服务
     *
     * @param ServiceName 接口的名称
     * @return 对应的服务
     */
    @Override
    public Object getServiceProvider(String ServiceName) {
        Object service = serviceMap.get(ServiceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
