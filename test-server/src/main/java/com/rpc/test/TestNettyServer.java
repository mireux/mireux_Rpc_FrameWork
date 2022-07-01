package com.rpc.test;

import com.rpc.annotation.AutoRegisterServiceScan;
import com.rpc.core.netty.server.NettyServer;
import com.rpc.utils.ConfigUtil;

import java.util.Properties;

import static com.rpc.core.registry.ServiceRegistry.NACOS_REGISTER;
import static com.rpc.core.registry.ServiceRegistry.ZOOKEEPER_REGISTER;
import static com.rpc.core.serializer.CommonSerializer.*;

@AutoRegisterServiceScan
public class TestNettyServer {

//    @AutoValue(applicationName = "mireux.rpc.server",configKey = "server.host")

    public static void main(String[] args) throws Exception {
        // 获取配置文件 获取对应的注册中心
        String host = null;
        String port = null;
        Integer register = null;
        Integer serializerCode = null;
        Properties propertiesConfig = ConfigUtil.getPropertiesConfig(TestNettyServer.class);
        String registry = (String) propertiesConfig.get("registry");
        if (registry.equals("nacos")) {
            ConfigUtil.getConfigByNacos("localhost", "mireux.rpc.server", "DEFAULT_GROUP");
            host = ConfigUtil.getConfig("mireux.rpc.server", "server.host");
            port = ConfigUtil.getConfig("mireux.rpc.server", "server.port");
            register = NACOS_REGISTER;
        } else if (registry.equals("zookeeper")) {
            host = (String) propertiesConfig.get("server.host");
            port = (String) propertiesConfig.get("server.port");
            register = ZOOKEEPER_REGISTER;
        }

        assert port != null;
        String serializer = (String) propertiesConfig.get("serializer");
        switch (serializer) {
            case "json":
                serializerCode = JSON_SERIALIZER;
                break;
            case "kryo":
                serializerCode = KRYO_SERIALIZER;
                break;
            case "protostuff":
                serializerCode = PROTOSTUFF_SERIALIZER;
                break;
        }
        NettyServer server = new NettyServer(host, Integer.parseInt(port), serializerCode, register);

        server.start();
    }

}
