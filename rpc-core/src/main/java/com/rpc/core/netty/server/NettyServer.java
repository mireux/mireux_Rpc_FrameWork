package com.rpc.core.netty.server;

import com.rpc.core.balancer.RoundRobinLoadBalancer;
import com.rpc.core.handler.AbstractRpcServer;
import com.rpc.core.netty.codec.CommonDecoder;
import com.rpc.core.netty.codec.CommonEncoder;
import com.rpc.core.provider.ServiceProviderImpl;
import com.rpc.core.registry.NacosService;
import com.rpc.core.serializer.CommonSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;
    private final CommonSerializer serializer;


    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosService(new RoundRobinLoadBalancer());
        serviceProvider = new ServiceProviderImpl();
        serializer = CommonSerializer.getByCode(serializerCode);
        //自动注册服务
        scanServices();
    }

    public void start() {
        // 处理连接的group
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // 处理连接后续的group
        NioEventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //配置ServerChannel参数，服务端接受连接的最大队列长度，如果队列已满，客户端连接将被拒绝。
                    .option(ChannelOption.SO_BACKLOG, 256)
                    //启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，默认的心跳间隔是7200s即2小时。
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //配置Channel参数，nodeLay没有延迟，true就代表禁用Nagle算法，减小传输延迟。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new CommonEncoder(serializer))  // 编码处理器
                                    .addLast(new CommonDecoder()) // 解码处理器
                                    .addLast(new NettyServerHandler()) // RpcRequest处理器
                                    .addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS)); // 心跳处理
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 阻塞关闭channel
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("服务器启动错误", e);
        } finally {
            // 优雅关闭Netty服务
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}
