package com.kabunx.component.autoconfigure.log;

import com.kabunx.component.log.aop.BizLogInterceptor;
import com.kabunx.component.log.aop.support.BizLogBeanFactoryPointcutAdvisor;
import com.kabunx.component.log.context.BizLogServiceHolder;
import com.kabunx.component.log.context.FunctionTemplateHolder;
import com.kabunx.component.log.parser.BizLogAnnotationParser;
import com.kabunx.component.log.parser.BizLogExpressionParser;
import com.kabunx.component.log.service.BizLogService;
import com.kabunx.component.log.service.OperatorService;
import com.kabunx.component.log.service.impl.DefaultBizLogServiceImpl;
import com.kabunx.component.log.service.impl.DefaultOperatorServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(FunctionTemplateHolder.class)
public class BizLogAutoConfiguration {

    @Bean
    FunctionTemplateHolder functionTemplateHolder() {
        return new FunctionTemplateHolder();
    }

    @Bean
    BizLogServiceHolder bizLogServiceHolder() {
        return new BizLogServiceHolder();
    }

    @Bean("defaultBizLogService")
    BizLogService defaultBizLogService() {
        return new DefaultBizLogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    OperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(BizLogExpressionParser.class)
    BizLogExpressionParser logTemplateParser() {
        return new BizLogExpressionParser();
    }

    @Bean
    BizLogInterceptor operationLogInterceptor(BizLogExpressionParser bizLogExpressionParser,
                                              OperatorService operatorService,
                                              BizLogServiceHolder bizLogServiceHolder) {
        BizLogInterceptor interceptor = new BizLogInterceptor(bizLogExpressionParser, operatorService, bizLogServiceHolder);
        interceptor.setTenantName("xxx");
        interceptor.setJoinTransaction(false);
        // ...
        return interceptor;
    }

    @Bean
    BizLogBeanFactoryPointcutAdvisor operationLogBeanFactoryPointcutAdvisor(BizLogInterceptor interceptor) {
        BizLogBeanFactoryPointcutAdvisor advisor = new BizLogBeanFactoryPointcutAdvisor();
        advisor.setLogRecordParser(new BizLogAnnotationParser());
        advisor.setAdvice(interceptor);
        return advisor;
    }

}
