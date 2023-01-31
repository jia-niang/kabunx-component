package com.kabunx.component.web.aspect;

import com.kabunx.component.common.util.JsonUtils;
import com.kabunx.component.web.dto.WebLog;
import com.kabunx.component.web.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一日志处理切面
 */
@Slf4j
@Aspect
public class AdapterLogAspect {
    /**
     * execution 代表要执行的表达式主体
     * 第一处 * 的位置代表方法返回的类型， * 表示返回所有类型
     * 第二处 * 的位置代表类名， * 表示所有类
     * 第三处 *(..) 的位置表示类的方法名及参数， * 表示任何方法，(..) 表示任何参数
     */
    @Pointcut("execution(public * com.kabunx.*.adapter.*.*.*(..)) || execution(public * com.kabunx.*.*.adapter.*.*.*(..))")
    public void webLogPointcut() {
    }

    @Before("webLogPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            WebLog webLog = new WebLog();
            webLog.setIp(RequestUtils.getClientIp(request));
            webLog.setMethod(request.getMethod());
            webLog.setUri(request.getRequestURI());
            webLog.setArgs(joinPoint.getArgs());
            webLog.setSignature(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            log.info("[AdapterLog] 请求信息 - {}", JsonUtils.object2Json(webLog));
        }
    }

    @AfterReturning(value = "webLogPointcut()", returning = "response")
    public void doAfterReturning(Object response) {
    }

    // 耗时记录
    @Around("webLogPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("[AdapterLog] 请求耗时 - {} ms", (int) (endTime - startTime));
        return result;
    }
}
