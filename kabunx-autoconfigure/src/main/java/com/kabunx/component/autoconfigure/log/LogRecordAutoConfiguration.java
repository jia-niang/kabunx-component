package com.kabunx.component.autoconfigure.log;

import com.kabunx.component.log.aop.LogRecordInterceptor;
import com.kabunx.component.log.aop.support.LogRecordBeanFactoryPointcutAdvisor;
import com.kabunx.component.log.context.FunctionTemplateHolder;
import com.kabunx.component.log.parser.LogRecordParser;
import com.kabunx.component.log.parser.LogRecordTemplateParser;
import com.kabunx.component.log.service.LogRecordService;
import com.kabunx.component.log.service.OperatorService;
import com.kabunx.component.log.service.impl.DefaultLogRecordServiceImpl;
import com.kabunx.component.log.service.impl.DefaultOperatorServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(FunctionTemplateHolder.class)
public class LogRecordAutoConfiguration {

    @Bean
    FunctionTemplateHolder functionTemplateHolder() {
        return new FunctionTemplateHolder();
    }

    @Bean
    @ConditionalOnMissingBean(LogRecordService.class)
    LogRecordService logRecordService() {
        return new DefaultLogRecordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    OperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(LogRecordTemplateParser.class)
    LogRecordTemplateParser logRecordTemplateParser(FunctionTemplateHolder functionTemplateHolder) {
        return new LogRecordTemplateParser(functionTemplateHolder);
    }

    @Bean
    LogRecordInterceptor logRecordInterceptor(LogRecordTemplateParser logRecordTemplateParser,
                                              LogRecordService logRecordService,
                                              OperatorService operatorGetService) {
        LogRecordInterceptor interceptor = new LogRecordInterceptor(logRecordTemplateParser, logRecordService, operatorGetService);
        interceptor.setTenant("xxx");
        interceptor.setJoinTransaction(false);
        // ...
        return interceptor;
    }

    @Bean
    LogRecordBeanFactoryPointcutAdvisor logRecordBeanFactoryPointcutAdvisor(LogRecordInterceptor interceptor) {
        LogRecordBeanFactoryPointcutAdvisor advisor = new LogRecordBeanFactoryPointcutAdvisor();
        advisor.setLogRecordParser(new LogRecordParser());
        advisor.setAdvice(interceptor);
        return advisor;
    }

}
