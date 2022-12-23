package com.kabunx.component.log.aop.support;

import com.kabunx.component.log.aop.BizLogPointcut;
import com.kabunx.component.log.parser.BizLogAnnotationParser;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.lang.NonNull;

/**
 *
 */
public class BizLogBeanFactoryPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private final BizLogPointcut pointcut = new BizLogPointcut();

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setLogRecordParser(BizLogAnnotationParser bizLogAnnotationParser) {
        pointcut.setBizLogAnnotationParser(bizLogAnnotationParser);
    }
}
