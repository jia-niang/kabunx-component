package com.kabunx.component.log.aop.support;

import com.kabunx.component.log.aop.OperationLogPointcut;
import com.kabunx.component.log.parser.OperationLogParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.NonNull;

public class OperationLogBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final OperationLogPointcut pointcut = new OperationLogPointcut();

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setLogRecordParser(OperationLogParser operationLogParser) {
        pointcut.setLogRecordParser(operationLogParser);
    }
}
