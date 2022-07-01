package com.rpc.core.netty.client;

import com.rpc.core.balancer.LoadBalancer;
import com.rpc.core.balancer.RandomLoadBalancer;
import com.rpc.core.balancer.RoundRobinLoadBalancer;
import com.rpc.core.handler.RpcClient;
import com.rpc.core.registry.LookUpService;
import com.rpc.core.serializer.CommonSerializer;
import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.RpcError;
import com.rpc.exception.RpcException;
import com.rpc.factory.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import static com.rpc.core.registry.ServiceRegistry.NACOS_REGISTER;
import static com.rpc.core.serializer.CommonSerializer.DEFAULT_SERIALIZER;

public class NettyClient implements RpcClient {


    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private CommonSerializer serializer;

    private final LookUpService lookUpService;
    private final UnprocessedRequests unprocessedRequests;

    // 完成多个构造器的选择
    public NettyClient() {
        //以默认序列化器调用构造函数
        this(DEFAULT_SERIALIZER, NACOS_REGISTER, new RandomLoadBalancer());
    }

    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }

    public NettyClient(Integer serializerCode, Integer registryCode) {
        // TODO 先暂时写死 后续可以选择从配置文件中读取
        this(serializerCode, registryCode, new RoundRobinLoadBalancer());
    }


    public NettyClient(Integer serializerCode, LoadBalancer loadBalancer) {
        this(serializerCode, NACOS_REGISTER, new RoundRobinLoadBalancer());
    }

    public NettyClient(Integer serializerCode, Integer registryCode, LoadBalancer loadBalancer) {
        lookUpService = LookUpService.getRegistry(registryCode);
        serializer = CommonSerializer.getByCode(serializerCode);
        unprocessedRequests = (UnprocessedRequests) SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = lookUpService.getService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (channel.isActive()) {
                //将新请求放入未处理完的请求中
                unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
                //向服务端发请求，并设置监听，关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        logger.info(String.format("客户端发送消息：%s", rpcRequest));
                    } else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        logger.error("发送消息时有错误发生:", future1.cause());

                    }
                });
            }
        } catch (Exception e) {
            //将请求从请求集合中移除
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
        }
        return resultFuture;
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
