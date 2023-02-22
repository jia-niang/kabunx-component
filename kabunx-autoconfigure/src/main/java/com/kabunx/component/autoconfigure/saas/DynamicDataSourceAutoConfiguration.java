package com.kabunx.component.autoconfigure.saas;


import com.kabunx.component.common.constant.OrderedConstants;
import com.kabunx.component.saas.datasource.DynamicDataSource;
import com.kabunx.component.saas.service.DataSourceService;
import com.kabunx.component.saas.service.MultiTenantService;
import com.kabunx.component.saas.service.impl.DataSourceServiceImpl;
import com.kabunx.component.saas.servlet.DataSourceHandlerInterceptor;
import com.kabunx.component.saas.servlet.MultiTenantHandlerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@ConditionalOnClass(DynamicDataSource.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class DynamicDataSourceAutoConfiguration {

    /**
     * 默认基础数据源
     *
     * @return 数据源
     */
    @Bean("defaultDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 自定义动态数据源
     *
     * @return 动态数据源
     */
    @Bean("dynamicDataSource")
    @ConditionalOnClass(DynamicDataSource.class)
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDataSource());
        // 默认数据源
        dynamicDataSource.setDefaultDataSource(defaultDataSource());
        // 动态数据源
        dynamicDataSource.setDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    /**
     * 修改 Mybatis 数据源配置
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dynamicDataSource());
        return factoryBean.getObject();
    }


    /**
     * 开启动态数据源 @Transactional 注解事务管理的支持
     */
    @Bean
    @ConditionalOnBean(DynamicDataSource.class)
    public PlatformTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean
    @ConditionalOnClass(DataSourceServiceImpl.class)
    @ConditionalOnBean({MultiTenantService.class, DynamicDataSource.class})
    public DataSourceService dataSourceService(MultiTenantService multiTenantService, DynamicDataSource dynamicDataSource) {
        return new DataSourceServiceImpl(multiTenantService, dynamicDataSource);
    }

    @Bean
    @Order(OrderedConstants.MULTI_TENANT)
    @ConditionalOnClass(MultiTenantHandlerInterceptor.class)
    public MultiTenantHandlerInterceptor multiTenantHandlerInterceptor() {
        return new MultiTenantHandlerInterceptor();
    }

    @Bean
    @Order(OrderedConstants.DYNAMIC_DATASOURCE)
    @ConditionalOnClass(DataSourceHandlerInterceptor.class)
    @ConditionalOnBean(DataSourceService.class)
    public DataSourceHandlerInterceptor dataSourceHandlerInterceptor(DataSourceService dataSourceService) {
        return new DataSourceHandlerInterceptor(dataSourceService);
    }
}
