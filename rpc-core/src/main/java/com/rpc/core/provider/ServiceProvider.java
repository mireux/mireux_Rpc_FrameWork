package com.rpc.core.provider;

/**
 * 保存和提供给
 */
public interface ServiceProvider {
    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String ServiceName);
}
