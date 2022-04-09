package com.rpc.core.registry;

import java.net.InetSocketAddress;

public interface LookUpService {

    /**
     * 根据用户名获取对应的具体服务
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress getService(String serviceName);
}
