package com.kabunx.component.saas.datasource;

import com.kabunx.component.saas.context.DataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义动态数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private Map<Object, Object> dataSources = new HashMap<>();

    /**
     * 获取当前数据源的键
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceKey();
    }

    /**
     * 获取当前数据源
     */
    @NonNull
    @Override
    protected DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
    }

    /**
     * 设置默认数据源
     *
     * @param defaultDataSource 默认数据源
     */
    public void setDefaultDataSource(Object defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
    }

    /**
     * 设置数据源
     *
     * @param dataSources 数据源
     */
    public void setDataSources(Map<Object, Object> dataSources) {
        this.dataSources = dataSources;
        super.setTargetDataSources(dataSources);
        // 保存多数据源的 keys
        DataSourceContextHolder.addDataSourceKeys(dataSources.keySet());
    }

    /**
     * 追加数据源
     *
     * @param key        租户标识
     * @param dataSource 数据源
     */
    public void addDataSource(String key, DataSource dataSource) {
        dataSources.put(key, dataSource);
        super.setTargetDataSources(dataSources);
        // 保存数据源的 key
        DataSourceContextHolder.addDataSourceKey(key);
        // 加载新的数据源
        super.afterPropertiesSet();
    }
}
