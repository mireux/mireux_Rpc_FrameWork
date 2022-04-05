package com.rpc.core.codec;


import com.rpc.core.serializer.CommonSerializer;
import com.rpc.entity.RpcRequest;
import com.rpc.entity.RpcResponse;
import com.rpc.enumeration.PackageType;
import com.rpc.enumeration.SerializerCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 进行编码拦截器 相当于实现我们自定义的协议
 * 这里也是自定义的协议
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 写入魔数
        out.writeInt(MAGIC_NUMBER);
        // 写入数据类型 RpcRequest or RpcResponse
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else if(msg instanceof RpcResponse) {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        // 选择序列化工具
        out.writeInt(serializer.getCode());
        // 序列化
        byte[] bytes = serializer.serialize(msg);
        // 数据长度
        out.writeInt(bytes.length);
        // 写入数据
        out.writeBytes(bytes);
    }
}
