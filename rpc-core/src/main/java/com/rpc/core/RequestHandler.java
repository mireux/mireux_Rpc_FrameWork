package com.rpc.core;

import com.rpc.core.provider.ServiceProvider;
import com.rpc.core.provider.ServiceProviderImpl;
import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实际执行方法调用的处理器
 */
public class RequestHandler {


    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }


    public Object handle(RpcRequest rpcRequest){

        Object result = null;
        try{
            result = invokeTargetMethod(rpcRequest, serviceProvider.getServiceProvider(rpcRequest.getInterfaceName()));
            logger.info("服务：{} 成功调用方法：{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        }catch (IllegalAccessException | InvocationTargetException e){
            logger.info("调用或发送时有错误发生：" + e);
        }
        //方法调用成功
        return RpcResponse.success(result, rpcRequest.getRequestId());
    }

    /**
     * 通过反射去调用服务对应的方法
     * @param rpcRequest Rpc请求
     * @param service 服务实体
     * @throws InvocationTargetException 当被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收。
     * @throws IllegalAccessException 无权访问
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            //getClass()获取的是实例对象的类型
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        }catch (NoSuchMethodException e){
            logger.info("调用或发送时有错误发生：" + e);
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
