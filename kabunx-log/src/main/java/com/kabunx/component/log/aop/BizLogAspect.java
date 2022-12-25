package com.kabunx.component.log.aop;

import com.kabunx.component.log.BizLogMonitor;
import com.kabunx.component.log.annotation.BizLog;
import com.kabunx.component.log.context.BizLogContextHolder;
import com.kabunx.component.log.parser.BizLogAspectParser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
public class BizLogAspect {

    private final BizLogAspectParser logAspectParser;

    public BizLogAspect(BizLogAspectParser logAspectParser) {
        this.logAspectParser = logAspectParser;
    }

    // 创建切点
    @Pointcut("@annotation(com.kabunx.component.log.annotation.BizLog)")
    public void bizLogMethod() {
    }

    // 在切点前后执行方法，通过 @annotation(bizLog) 绑定注解到第二个参数
    // ProceedingJoinPoint 必须要放在第一个参数
    @Around("bizLogMethod() && @annotation(bizLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, BizLog bizLog) throws Throwable {
        BizLogContextHolder.empty();
        Object result = joinPoint.proceed();
        StopWatch stopWatch = new StopWatch(BizLogMonitor.MONITOR_NAME);
        stopWatch.start(BizLogMonitor.MONITOR_TASK_AFTER_EXECUTE);
        try {
            log.info(logAspectParser.parse(joinPoint, bizLog::success, result, "", String.class));
        } catch (Exception e) {
            log.error("[BizLog] handle biz log exception", e);
        } finally {
            BizLogContextHolder.clear();
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        }
        return result;
    }
}
