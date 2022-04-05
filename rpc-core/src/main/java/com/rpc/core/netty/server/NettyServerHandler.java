package com.rpc.core.netty.server;

import com.rpc.core.RequestHandler;
import com.rpc.core.registry.DefaultServiceRegistry;
import com.rpc.core.registry.ServiceRegistry;
import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 处理接受到的RpcRequest
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static final RequestHandler requestHandler;
    private static final ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("服务端接收到服务:{}", rpcRequest);
            String interfaceName = rpcRequest.getInterfaceName();
            // 进行注册
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            ChannelFuture channelFuture = ctx.writeAndFlush(RpcResponse.success(result,rpcRequest.getRequestId()));
            logger.info("发送RpcResponse");
            // 监听 如果所有数据包发送完 关闭通道
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生：");
        cause.printStackTrace();
        ctx.close();
    }


}
