package com.kabunx.component.log.aop.support;

import com.kabunx.component.log.aop.LogRecordPointcut;
import com.kabunx.component.log.parser.LogRecordParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.NonNull;

public class LogRecordBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final LogRecordPointcut pointcut = new LogRecordPointcut();

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setLogRecordParser(LogRecordParser logRecordParser) {
        pointcut.setLogRecordParser(logRecordParser);
    }
}
