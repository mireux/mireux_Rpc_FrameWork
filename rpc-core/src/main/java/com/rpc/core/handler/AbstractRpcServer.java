package com.rpc.core.handler;

import com.rpc.annotation.AutoRegisterService;
import com.rpc.annotation.AutoRegisterServiceScan;
import com.rpc.core.provider.ServiceProvider;
import com.rpc.core.registry.ServiceRegistry;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.utils.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 扫描服务类并进行服务注册
 */
public class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        // 获取main()入口所在类的类名，即启动类
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            // 获取启动类对应的实例对象
            startClass = Class.forName(mainClassName);
            // 判断启动类是否存在ServiceScan注解
            if (!startClass.isAnnotationPresent(AutoRegisterServiceScan.class)) {
                logger.error("启动类缺少@AutoRegisterServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.info("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        // 获取ServiceScan注解接口对应value()的值 默认为设置的""
        String basePackagePath = startClass.getAnnotation(AutoRegisterServiceScan.class).value();
        if ("".equals(basePackagePath)) {
            // 获取启动类所在的包，因为服务类也放在这个包下面
            basePackagePath = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 获取包下面的所有类Class对象
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackagePath);
        for (Class<?> clazz : classSet) {
            // 利用AutoRegisterService注解
            if (clazz.isAnnotationPresent(AutoRegisterService.class)) {
                // 获取Service注解接口对应的name()的值，默认设置的为""
                String serviceName = clazz.getAnnotation(AutoRegisterService.class).name();
                Object obj;
                try {
                    // 创建Impl类的实例
                    obj = clazz.getDeclaredConstructor().newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    logger.error("创建" + clazz + "时有错误发生");
                    continue;
                }
                if ("".equals(serviceName)) {
                    // 一个服务Impl类可能实现了多个服务接口
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        publishService(obj, anInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }


    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
