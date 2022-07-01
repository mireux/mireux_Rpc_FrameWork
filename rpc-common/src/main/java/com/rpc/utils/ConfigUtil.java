package com.rpc.utils;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.rpc.exception.RpcException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executor;

import static com.rpc.enumeration.RpcError.GET_CONFIG_FAIL;

/**
 * 通过nacos的配置中心获取配置
 */
public class ConfigUtil {
    /* 将对应的信息以key-value的形式存入 根据传入的名字获取到对应的配置
        这里我们暂时将dataId作为依据
    */
    public static Map<String, Map<String, String>> groupMap = new HashMap<>();

    private static final Properties configProperties = new Properties();
    public static InputStream is;

    /**
     * 获取到对应的配置信息
     *
     * @param serverAddr 服务器地址
     * @param dataId     配置 ID
     * @param group      配置分组
     * @throws NacosException
     */
    public static void getConfigByNacos(String serverAddr, String dataId, String group) throws NacosException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        String content = configService.getConfig(dataId, group, 5000);
        // 分解回传的配置字符串 放入configMap中
        Map<String, String> configMap = groupMap.getOrDefault(dataId, new HashMap<>());
        int idx = 0;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            // 如果读取到'/r'说明换行了
            if (content.charAt(i) == '\n') {
                list.add(content.substring(0, i));
                idx = i + 1;
            }
        }
        list.add(content.substring(idx));
        list.forEach(str -> {
            String[] split = str.split("=");
            configMap.put(split[0], split[1]);
        });
        groupMap.put(dataId, configMap);
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("receive:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }

    public static String getConfig(String applicationName, String configKey) {
        Map<String, String> configMap = groupMap.get(applicationName);
        if (configMap == null || configMap.isEmpty()) {
            // 说明没有配置 或者 没有去获取配置
            throw new RpcException(GET_CONFIG_FAIL);
        }
        return configMap.get(configKey);
    }

    public static <T> Properties getPropertiesConfig(Class<T> clazz) {
        if (configProperties.isEmpty()) {
            is = clazz.getResourceAsStream("/config.properties");
            Set<Map.Entry<Object, Object>> entries;
            try {
                configProperties.load(is);
                entries = configProperties.entrySet();
//            for (Map.Entry<Object, Object> entry : entries) {
//
//                String value = (String) entry.getValue();
//                value = URLEncoder.encode(value, StandardCharsets.ISO_8859_1);
//                value = URLDecoder.decode(value, "GBK");
//
//                System.out.println(entry.getKey() + "------" + value);
//            }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configProperties;
    }


}
