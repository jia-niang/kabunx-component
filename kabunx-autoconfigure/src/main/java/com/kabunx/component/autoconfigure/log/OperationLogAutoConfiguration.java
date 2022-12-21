package com.kabunx.component.autoconfigure.log;

import com.kabunx.component.log.aop.OperationLogInterceptor;
import com.kabunx.component.log.aop.support.OperationLogBeanFactoryPointcutAdvisor;
import com.kabunx.component.log.context.FunctionTemplateHolder;
import com.kabunx.component.log.parser.OperationLogParser;
import com.kabunx.component.log.parser.LogTemplateParser;
import com.kabunx.component.log.service.OperationLogService;
import com.kabunx.component.log.service.OperatorService;
import com.kabunx.component.log.service.impl.DefaultOperationLogServiceImpl;
import com.kabunx.component.log.service.impl.DefaultOperatorServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(FunctionTemplateHolder.class)
public class OperationLogAutoConfiguration {

    @Bean
    FunctionTemplateHolder functionTemplateHolder() {
        return new FunctionTemplateHolder();
    }

    @Bean
    @ConditionalOnMissingBean(OperationLogService.class)
    OperationLogService operationLogService() {
        return new DefaultOperationLogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    OperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LogTemplateParser.class)
    LogTemplateParser logTemplateParser(FunctionTemplateHolder functionTemplateHolder) {
        return new LogTemplateParser(functionTemplateHolder);
    }

    @Bean
    OperationLogInterceptor operationLogInterceptor(LogTemplateParser logTemplateParser,
                                                 OperationLogService operationLogService,
                                                 OperatorService operatorService) {
        OperationLogInterceptor interceptor = new OperationLogInterceptor(logTemplateParser, operationLogService, operatorService);
        interceptor.setTenantName("xxx");
        interceptor.setJoinTransaction(false);
        // ...
        return interceptor;
    }

    @Bean
    OperationLogBeanFactoryPointcutAdvisor operationLogBeanFactoryPointcutAdvisor(OperationLogInterceptor interceptor) {
        OperationLogBeanFactoryPointcutAdvisor advisor = new OperationLogBeanFactoryPointcutAdvisor();
        advisor.setLogRecordParser(new OperationLogParser());
        advisor.setAdvice(interceptor);
        return advisor;
    }

}
