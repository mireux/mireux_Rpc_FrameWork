package com.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    // 请求号
    private String requestId;

    // 接口名
    private String interfaceName;

    // 调用方法名
    private String methodName;

    // 待调用的参数
    private Object[] parameters;

    // 待调用的参数类型
    private Class<?>[] paramTypes;
}
