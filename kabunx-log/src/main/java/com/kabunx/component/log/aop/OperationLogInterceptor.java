package com.kabunx.component.log.aop;

import com.kabunx.component.log.dto.CodeVariableType;
import com.kabunx.component.log.OperationLogMonitor;
import com.kabunx.component.log.parser.OperationLogParser;
import com.kabunx.component.log.dto.MethodExecuteResult;
import com.kabunx.component.log.context.OperationLogContext;
import com.kabunx.component.log.dto.OperationLogEntity;
import com.kabunx.component.log.dto.OperationLogMetadata;
import com.kabunx.component.log.parser.LogTemplateParser;
import com.kabunx.component.log.service.OperationLogService;
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
public class OperationLogInterceptor implements MethodInterceptor {

    private final OperationLogParser operationLogParser = new OperationLogParser();

    private final LogTemplateParser logTemplateParser;

    private final OperationLogService operationLogService;

    private final OperatorService operatorService;

    private String tenantName;

    private boolean joinTransaction;

    public OperationLogInterceptor(LogTemplateParser logTemplateParser,
                                   OperationLogService operationLogService,
                                   OperatorService operatorService) {
        this.logTemplateParser = logTemplateParser;
        this.operationLogService = operationLogService;
        this.operatorService = operatorService;
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
        StopWatch stopWatch = new StopWatch(OperationLogMonitor.MONITOR_NAME);
        stopWatch.start(OperationLogMonitor.MONITOR_TASK_BEFORE_EXECUTE);
        Class<?> targetClass = getTargetClass(target);
        Object result = null;
        MethodExecuteResult methodExecuteResult = new MethodExecuteResult(method, args, targetClass);
        OperationLogContext.empty();
        Collection<OperationLogMetadata> operations = new ArrayList<>();
        Map<String, String> functionNameAndReturnMap = new HashMap<>();
        try {
            operations = operationLogParser.buildLogRecordOperations(method, targetClass);
            List<String> spElTemplates = getBeforeExecuteFunctionTemplate(operations);
            functionNameAndReturnMap = logTemplateParser.processBeforeExecuteFunctionTemplate(spElTemplates, targetClass, method, args);
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
        stopWatch.start(OperationLogMonitor.MONITOR_TASK_AFTER_EXECUTE);
        try {
            if (!CollectionUtils.isEmpty(operations)) {
                logExecute(methodExecuteResult, functionNameAndReturnMap, operations);
            }
        } catch (Exception t) {
            log.error("log record parse exception", t);
            throw t;
        } finally {
            OperationLogContext.clear();
            stopWatch.stop();
            log.info(stopWatch.toString());
        }
        if (Objects.nonNull(methodExecuteResult.getThrowable())) {
            throw methodExecuteResult.getThrowable();
        }
        return result;
    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<OperationLogMetadata> operations) {
        List<String> spElTemplates = new ArrayList<>();
        for (OperationLogMetadata operation : operations) {
            // 执行之前的函数，失败模版不解析
            List<String> templates = getSpElTemplates(operation, operation.getSuccessTemplate());
            if (!CollectionUtils.isEmpty(templates)) {
                spElTemplates.addAll(templates);
            }
        }
        return spElTemplates;
    }

    private void logExecute(MethodExecuteResult methodExecuteResult,
                            Map<String, String> functionNameAndReturnMap,
                            Collection<OperationLogMetadata> operations) {
        for (OperationLogMetadata operation : operations) {
            try {
                if (StringUtils.isEmpty(operation.getSuccessTemplate()) && StringUtils.isEmpty(operation.getFailTemplate())) {
                    continue;
                }
                if (exitsCondition(methodExecuteResult, functionNameAndReturnMap, operation)) {
                    continue;
                }
                if (!methodExecuteResult.isSuccess()) {
                    failLogExecute(methodExecuteResult, functionNameAndReturnMap, operation);
                } else {
                    successLogExecute(methodExecuteResult, functionNameAndReturnMap, operation);
                }
            } catch (Exception t) {
                log.error("log record execute exception", t);
                if (joinTransaction) {
                    throw t;
                }
            }
        }
    }

    private void successLogExecute(MethodExecuteResult methodExecuteResult,
                                   Map<String, String> functionNameAndReturnMap,
                                   OperationLogMetadata operation) {
        // 若存在 success 条件模版，解析出成功/失败的模版
        String detail;
        boolean flag = true;
        if (!StringUtils.isEmpty(operation.getSuccess())) {
            String condition = logTemplateParser.singleProcessTemplate(methodExecuteResult, operation.getSuccess(), functionNameAndReturnMap);
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
        String operator = getOperatorFromServiceAndPutTemplate(operation, spElTemplates);
        Map<String, String> expressionValues = logTemplateParser.processTemplate(spElTemplates, methodExecuteResult, functionNameAndReturnMap);
        saveLog(methodExecuteResult.getMethod(), !flag, operation, operator, detail, expressionValues);
    }

    private void failLogExecute(MethodExecuteResult methodExecuteResult, Map<String, String> functionNameAndReturnMap,
                                OperationLogMetadata operation) {
        if (StringUtils.isEmpty(operation.getFailTemplate())) {
            return;
        }
        String detail = operation.getFailTemplate();
        List<String> spElTemplates = getSpElTemplates(operation, detail);
        String operatorIdFromService = getOperatorFromServiceAndPutTemplate(operation, spElTemplates);

        Map<String, String> expressionValues = logTemplateParser.processTemplate(spElTemplates, methodExecuteResult, functionNameAndReturnMap);
        saveLog(methodExecuteResult.getMethod(), true, operation, operatorIdFromService, detail, expressionValues);
    }

    private boolean exitsCondition(MethodExecuteResult methodExecuteResult,
                                   Map<String, String> functionNameAndReturnMap,
                                   OperationLogMetadata operation) {
        if (!StringUtils.isEmpty(operation.getCondition())) {
            String condition = logTemplateParser.singleProcessTemplate(methodExecuteResult, operation.getCondition(), functionNameAndReturnMap);
            return StringUtils.endsWithIgnoreCase(condition, "false");
        }
        return false;
    }

    private void saveLog(Method method, boolean flag, OperationLogMetadata operation, String operatorIdFromService,
                         String detail, Map<String, String> expressionValues) {
        if (StringUtils.isEmpty(expressionValues.get(detail))) {
            return;
        }
        if (Objects.equals(detail, expressionValues.get(detail))) {
            log.warn("模板没有被解析！");
        }
        OperationLogEntity operationLogEntity = OperationLogEntity.builder()
                .tenantName(tenantName)
                .type(expressionValues.get(operation.getType()))
                .bizNo(expressionValues.get(operation.getBizNo()))
                .operator(getRealOperatorId(operation, operatorIdFromService, expressionValues))
                .subType(expressionValues.get(operation.getSubType()))
                .extra(expressionValues.get(operation.getExtra()))
                .codeVariable(getCodeVariable(method))
                .detail(expressionValues.get(detail))
                .count(1) // TODO 替换为实际值
                .fail(flag)
                .createTime(new Date())
                .build();
        // 保存
        operationLogService.save(operationLogEntity);
    }

    private Map<CodeVariableType, Object> getCodeVariable(Method method) {
        Map<CodeVariableType, Object> map = new HashMap<>();
        map.put(CodeVariableType.ClassName, method.getDeclaringClass());
        map.put(CodeVariableType.MethodName, method.getName());
        return map;
    }

    private List<String> getSpElTemplates(OperationLogMetadata operation, String... actions) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(operation.getType());
        spElTemplates.add(operation.getBizNo());
        spElTemplates.add(operation.getSubType());
        spElTemplates.add(operation.getExtra());
        spElTemplates.addAll(Arrays.asList(actions));
        return spElTemplates;
    }

    private String getRealOperatorId(OperationLogMetadata operation, String operator, Map<String, String> expressionValues) {
        return !StringUtils.isEmpty(operator) ? operator : expressionValues.get(operation.getOperator());
    }

    private String getOperatorFromServiceAndPutTemplate(OperationLogMetadata operation, List<String> spElTemplates) {
        String realOperator = "";
        if (StringUtils.isEmpty(operation.getOperator())) {
            realOperator = operatorService.getCurrentAuth().getUsername();
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

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setJoinTransaction(boolean joinTransaction) {
        this.joinTransaction = joinTransaction;
    }
}
