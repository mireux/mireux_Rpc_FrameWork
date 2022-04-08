package com.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200,"调用方法成功"),
    FAIL(500,"调用方法失败"),
    NOT_FOUND_METHOD(501,"未找到指定方法"),
    NOT_FOUND_CLASS(502,"未找到指定类"), METHOD_NOT_FOUND(503, "未找到对应的方法");

    private final int code;
    private final String message;
}