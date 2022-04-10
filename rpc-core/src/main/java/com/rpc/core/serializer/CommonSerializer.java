package com.rpc.core.serializer;

public interface CommonSerializer {
    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;
     int DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }

}
