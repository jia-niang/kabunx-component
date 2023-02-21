package com.kabunx.component.saas.aop;


import com.kabunx.component.common.exception.BizException;
import com.kabunx.component.saas.SaaSErrorInfo;
import com.kabunx.component.saas.annotation.DataSource;
import com.kabunx.component.saas.context.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StringUtils;

/**
 * 数据库切面
 */
@Slf4j
@Aspect
public class DataSourceAspect {
    // 创建切点
    @Pointcut("@annotation(com.kabunx.component.saas.annotation.DataSource)")
    public void dataSourceMethod() {
    }

    // 在切点前后执行方法，通过 @annotation(dynamicDataSource) 绑定注解到第二个参数
    // ProceedingJoinPoint 必须要放在第一个参数
    @Around("dataSourceMethod() && @annotation(dataSource)")
    public Object doAround(ProceedingJoinPoint joinPoint, DataSource dataSource) throws Throwable {
        String dataSourceKey = DataSourceContextHolder.getDataSourceKey();
        String tmpDataSourceKey = dataSource.key();
        if (!StringUtils.hasText(tmpDataSourceKey)) {
            log.error("[SaaS] 注解未定义数据源");
            throw new BizException(SaaSErrorInfo.DATASOURCE_UNDEFINED);
        }
        if (!DataSourceContextHolder.containDataSourceKey(tmpDataSourceKey)) {
            log.error("[SaaS] 数据源不存在");
            throw new BizException(SaaSErrorInfo.DATASOURCE_NOT_FOUND);
        }
        DataSourceContextHolder.setDataSourceKey(tmpDataSourceKey);
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            log.error("[SaaS] datasource - {}  exception", tmpDataSourceKey, ex);
            throw ex;
        } finally {
            DataSourceContextHolder.setDataSourceKey(dataSourceKey);
        }
    }
}
