package com.kabunx.component.saas.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> dataSourceContext = TransmittableThreadLocal.withInitial(() -> "default");

    /**
     * 数据源key的集合
     */
    public static Set<Object> dataSourceKeys = new HashSet<>();

    /**
     * 切换数据源
     *
     * @param key 数据源标识
     */
    public static void setDataSourceKey(String key) {
        dataSourceContext.set(key);
    }

    /**
     * 获取数据源
     *
     * @return key
     */
    public static String getDataSourceKey() {
        return dataSourceContext.get();
    }

    /**
     * 重置数据源
     */
    public static void removeDataSourceKey() {
        dataSourceContext.remove();
    }

    /**
     * 判断数据源是否存在
     *
     * @param key 数据源key
     * @return 是否存在该数据源
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }

    /**
     * 添加数据源key
     */
    public static void addDataSourceKey(Object key) {
        dataSourceKeys.add(key);
    }

    /**
     * 添加多个数据源 keys
     *
     * @param keys 多数据源
     */
    public static void addDataSourceKeys(Collection<? extends Object> keys) {
        dataSourceKeys.addAll(keys);
    }
}
