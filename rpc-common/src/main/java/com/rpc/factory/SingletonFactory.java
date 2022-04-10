package com.rpc.factory;


import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 */
public class SingletonFactory {

    // 缓存 存放单例
    private static volatile Map<Class, Object> singletonMap = new HashMap<>();

    private SingletonFactory() {
    }

    /*
     * 这里我们采用dcl模式的单例模式
     */
    public static <T> Object getInstance(Class<T> clazz) {
        Object instance = singletonMap.get(clazz);
        if (instance == null) {
            synchronized (SingletonFactory.class) {
                if (instance == null) {
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                        singletonMap.put(clazz, instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }

            }
        }
        return instance;
    }
}
