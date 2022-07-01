package com.rpc.core.serializer;

import com.rpc.enumeration.SerializerCode;

public class ProtobufSerializer implements CommonSerializer {
    @Override
    public byte[] serialize(Object obj) {
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return null;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }
}
