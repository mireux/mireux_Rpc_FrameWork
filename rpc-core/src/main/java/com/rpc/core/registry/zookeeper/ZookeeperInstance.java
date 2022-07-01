package com.rpc.core.registry.zookeeper;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZookeeperInstance implements Serializable {

    private String host;

    private int port;

    private String serviceName;


    public String toString() {
        return host + "|" + port + "|" + serviceName;
    }

//    private
}
