package com.kabunx.component.log.aop;

import com.kabunx.component.log.dto.CodeVariableType;
import com.kabunx.component.log.LogRecordMonitor;
import com.kabunx.component.log.parser.LogRecordParser;
import com.kabunx.component.log.dto.MethodExecuteResult;
import com.kabunx.component.log.context.LogRecordContext;
import com.kabunx.component.log.dto.LogRecordEntity;
import com.kabunx.component.log.dto.LogRecordOperation;
import com.kabunx.component.log.parser.LogRecordTemplateParser;
import com.kabunx.component.log.service.LogRecordService;
import com.kabunx.component.log.service.OperatorService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class LogRecordInterceptor implements MethodInterceptor {

    private final LogRecordParser logRecordParser = new LogRecordParser();

    private final LogRecordTemplateParser logRecordTemplateParser;

    private final LogRecordService logRecordService;

    private final OperatorService operatorGetService;

    private String tenantId;

    private boolean joinTransaction;

    public LogRecordInterceptor(LogRecordTemplateParser logRecordTemplateParser,
                                LogRecordService logRecordService,
                                OperatorService operatorGetService) {
        this.logRecordTemplateParser = logRecordTemplateParser;
        this.logRecordService = logRecordService;
        this.operatorGetService = operatorGetService;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        return execute(invocation, invocation.getThis(), method, invocation.getArguments());
    }

    private Object execute(MethodInvocation invoker, Object target, Method method, Object[] args) throws Throwable {
        // 代理不拦截
        if (AopUtils.isAopProxy(target)) {
            return invoker.proceed();
        }
        // 性能监控
        StopWatch stopWatch = new StopWatch(LogRecordMonitor.MONITOR_NAME);
        stopWatch.start(LogRecordMonitor.MONITOR_TASK_BEFORE_EXECUTE);
        Class<?> targetClass = getTargetClass(target);
        Object result = null;
        MethodExecuteResult methodExecuteResult = new MethodExecuteResult(method, args, targetClass);
        LogRecordContext.empty();
        Collection<LogRecordOperation> operations = new ArrayList<>();
        Map<String, String> functionNameAndReturnMap = new HashMap<>();
        try {
            operations = logRecordParser.buildLogRecordOperations(method, targetClass);
            List<String> spElTemplates = getBeforeExecuteFunctionTemplate(operations);
            functionNameAndReturnMap = logRecordTemplateParser.processBeforeExecuteFunctionTemplate(spElTemplates, targetClass, method, args);
        } catch (Exception e) {
            log.error("log record parse before function exception", e);
        } finally {
            stopWatch.stop();
        }
        // 程序处理
        try {
            result = invoker.proceed();
            methodExecuteResult.setResult(result);
            methodExecuteResult.setSuccess(true);
        } catch (Exception e) {
            methodExecuteResult.setSuccess(false);
            methodExecuteResult.setThrowable(e);
            methodExecuteResult.setErrorMsg(e.getMessage());
        }
        stopWatch.start(LogRecordMonitor.MONITOR_TASK_AFTER_EXECUTE);
        try {
            if (!CollectionUtils.isEmpty(operations)) {
                recordExecute(methodExecuteResult, functionNameAndReturnMap, operations);
            }
        } catch (Exception t) {
            log.error("log record parse exception", t);
            throw t;
        } finally {
            LogRecordContext.clear();
            stopWatch.stop();
            log.info(stopWatch.toString());
        }
        if (methodExecuteResult.getThrowable() != null) {
            throw methodExecuteResult.getThrowable();
        }
        return result;
    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<LogRecordOperation> operations) {
        List<String> spElTemplates = new ArrayList<>();
        for (LogRecordOperation operation : operations) {
            // 执行之前的函数，失败模版不解析
            List<String> templates = getSpElTemplates(operation, operation.getSuccessTemplate());
            if (!CollectionUtils.isEmpty(templates)) {
                spElTemplates.addAll(templates);
            }
        }
        return spElTemplates;
    }

    private void recordExecute(MethodExecuteResult methodExecuteResult,
                               Map<String, String> functionNameAndReturnMap,
                               Collection<LogRecordOperation> operations) {
        for (LogRecordOperation operation : operations) {
            try {
                if (StringUtils.isEmpty(operation.getSuccessTemplate()) && StringUtils.isEmpty(operation.getFailTemplate())) {
                    continue;
                }
                if (exitsCondition(methodExecuteResult, functionNameAndReturnMap, operation)) {
                    continue;
                }
                if (!methodExecuteResult.isSuccess()) {
                    failRecordExecute(methodExecuteResult, functionNameAndReturnMap, operation);
                } else {
                    successRecordExecute(methodExecuteResult, functionNameAndReturnMap, operation);
                }
            } catch (Exception t) {
                log.error("log record execute exception", t);
                if (joinTransaction) {
                    throw t;
                }
            }
        }
    }

    private void successRecordExecute(MethodExecuteResult methodExecuteResult,
                                      Map<String, String> functionNameAndReturnMap,
                                      LogRecordOperation operation) {
        // 若存在 success 条件模版，解析出成功/失败的模版
        String detail;
        boolean flag = true;
        if (!StringUtils.isEmpty(operation.getSuccess())) {
            String condition = logRecordTemplateParser.singleProcessTemplate(methodExecuteResult, operation.getSuccess(), functionNameAndReturnMap);
            if (StringUtils.endsWithIgnoreCase(condition, "true")) {
                detail = operation.getSuccessTemplate();
            } else {
                detail = operation.getFailTemplate();
                flag = false;
            }
        } else {
            detail = operation.getSuccessTemplate();
        }
        if (StringUtils.isEmpty(detail)) {
            // 没有日志内容则忽略
            return;
        }
        List<String> spElTemplates = getSpElTemplates(operation, detail);
        String operatorIdFromService = getOperatorFromServiceAndPutTemplate(operation, spElTemplates);
        Map<String, String> expressionValues = logRecordTemplateParser.processTemplate(spElTemplates, methodExecuteResult, functionNameAndReturnMap);
        saveLog(methodExecuteResult.getMethod(), !flag, operation, operatorIdFromService, detail, expressionValues);
    }

    private void failRecordExecute(MethodExecuteResult methodExecuteResult, Map<String, String> functionNameAndReturnMap,
                                   LogRecordOperation operation) {
        if (StringUtils.isEmpty(operation.getFailTemplate())) {
            return;
        }
        String detail = operation.getFailTemplate();
        List<String> spElTemplates = getSpElTemplates(operation, detail);
        String operatorIdFromService = getOperatorFromServiceAndPutTemplate(operation, spElTemplates);

        Map<String, String> expressionValues = logRecordTemplateParser.processTemplate(spElTemplates, methodExecuteResult, functionNameAndReturnMap);
        saveLog(methodExecuteResult.getMethod(), true, operation, operatorIdFromService, detail, expressionValues);
    }

    private boolean exitsCondition(MethodExecuteResult methodExecuteResult,
                                   Map<String, String> functionNameAndReturnMap,
                                   LogRecordOperation operation) {
        if (!StringUtils.isEmpty(operation.getCondition())) {
            String condition = logRecordTemplateParser.singleProcessTemplate(methodExecuteResult, operation.getCondition(), functionNameAndReturnMap);
            return StringUtils.endsWithIgnoreCase(condition, "false");
        }
        return false;
    }

    private void saveLog(Method method, boolean flag, LogRecordOperation operation, String operatorIdFromService,
                         String detail, Map<String, String> expressionValues) {
        if (StringUtils.isEmpty(expressionValues.get(detail))) {
            return;
        }
        if (Objects.equals(detail, expressionValues.get(detail))) {
            log.warn("模板没有被解析！");
        }
        LogRecordEntity logRecordEntity = LogRecordEntity.builder()
                .tenant(tenantId)
                .type(expressionValues.get(operation.getType()))
                .bizNo(expressionValues.get(operation.getBizNo()))
                .operator(getRealOperatorId(operation, operatorIdFromService, expressionValues))
                .subType(expressionValues.get(operation.getSubType()))
                .extra(expressionValues.get(operation.getExtra()))
                .codeVariable(getCodeVariable(method))
                .detail(expressionValues.get(detail))
                .fail(flag)
                .createTime(new Date())
                .build();
        // 保存
        logRecordService.save(logRecordEntity);
    }

    private Map<CodeVariableType, Object> getCodeVariable(Method method) {
        Map<CodeVariableType, Object> map = new HashMap<>();
        map.put(CodeVariableType.ClassName, method.getDeclaringClass());
        map.put(CodeVariableType.MethodName, method.getName());
        return map;
    }

    private List<String> getSpElTemplates(LogRecordOperation operation, String... actions) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(operation.getType());
        spElTemplates.add(operation.getBizNo());
        spElTemplates.add(operation.getSubType());
        spElTemplates.add(operation.getExtra());
        spElTemplates.addAll(Arrays.asList(actions));
        return spElTemplates;
    }

    private String getRealOperatorId(LogRecordOperation operation, String operatorIdFromService, Map<String, String> expressionValues) {
        return !StringUtils.isEmpty(operatorIdFromService) ? operatorIdFromService : expressionValues.get(operation.getOperator());
    }

    private String getOperatorFromServiceAndPutTemplate(LogRecordOperation operation, List<String> spElTemplates) {
        String realOperator = "";
        if (StringUtils.isEmpty(operation.getOperator())) {
            realOperator = operatorGetService.getCurrentAuth().getUsername();
            if (StringUtils.isEmpty(realOperator)) {
                throw new IllegalArgumentException("[LogRecord] operator is null");
            }
        } else {
            spElTemplates.add(operation.getOperator());
        }
        return realOperator;
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    public void setTenant(String tenant) {
        this.tenantId = tenant;
    }

    public void setJoinTransaction(boolean joinTransaction) {
        this.joinTransaction = joinTransaction;
    }
}
