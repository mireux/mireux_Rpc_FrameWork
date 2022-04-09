package com.rpc.core.provider;

/**
 * 保存和提供给
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service,Class<T> serviceClass);

    Object getServiceProvider(String ServiceName);
}
