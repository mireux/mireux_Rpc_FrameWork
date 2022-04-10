package com.rpc.core.netty.client;

import com.rpc.core.handler.RpcClient;
import com.rpc.core.registry.NacosService;
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

public class NettyClient implements RpcClient {


    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private CommonSerializer serializer;
    private final NacosService nacosService;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        // 单例模式创建 unprocessedRequests
        unprocessedRequests = (UnprocessedRequests) SingletonFactory.getInstance(UnprocessedRequests.class);
        nacosService = new NacosService();
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = nacosService.getService(rpcRequest.getInterfaceName());
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
