package com.rpc.entity;

import com.rpc.enumeration.ResponseCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {

    /**
     * 请求号
     */
    private String requestId;
    /**
     *响应状态码
     */
    private Integer statusCode;
    /**
     *响应状态码对应的信息
     */
    private String message;
    /**
     *成功时的响应数据
     */
    private T data;

    /**
     *  成功时服务端返回的对象
     */
    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setRequestId(requestId);
        response.setData(data);
        System.out.println("response = " + response);
        return response;
    }

    /**
     *  失败时服务端返回的对象
     */
    public static <T> RpcResponse<T> fail(ResponseCode code,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(code.getCode());
        response.setRequestId(requestId);
        response.setMessage(code.getMessage());
        return response;
    }
}