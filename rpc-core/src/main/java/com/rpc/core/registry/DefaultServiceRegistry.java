package com.rpc.core.registry;

import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认服务注册表
 */
public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    /**
     * key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
     */
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    /**
     * 用来存放实现类的名称，Set存取更高效，存放实现类名称相比存放接口名称占的空间更小，因为一个实现类可能实现了多个接口，查找效率也会更高
     */
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public <T> void register(T service) {
        /*
         * getCanonicalName() 是获取所传类从java语言规范定义的格式输出。
         * 和getName() 没有很大的区别
         */
        String serviceName = service.getClass().getCanonicalName();
        // 如果已经存在该服务 就不进行注册
        if(registeredService.contains(serviceName)) {
            return ;
        }
        registeredService.add(serviceName);
        // 根据服务名获取对应的接口 可能实现多个接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        //注册到map中
        for (Class<?> inter : interfaces) {
            serviceMap.put(inter.getCanonicalName(),service);
        }
        logger.info("向接口：{} 注册服务：{}", interfaces, serviceName);
    }

    @Override
    public <T> void register(String serviceName, InetSocketAddress inetSocketAddress) {

    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
