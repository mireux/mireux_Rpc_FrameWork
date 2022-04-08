package com.rpc.core.netty.provider;

/**
 * 保存和提供给
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service);

    Object getServiceProvider(String ServiceName);
}
