package com.kabunx.component.log.aop;

import com.kabunx.component.log.context.BizLogServiceHolder;
import com.kabunx.component.log.dto.CodeVariableType;
import com.kabunx.component.log.BizLogMonitor;
import com.kabunx.component.log.parser.BizLogAnnotationParser;
import com.kabunx.component.log.dto.MethodExecute;
import com.kabunx.component.log.context.BizLogContext;
import com.kabunx.component.log.dto.BizLogEntity;
import com.kabunx.component.log.dto.BizLogExpression;
import com.kabunx.component.log.parser.BizLogExpressionParser;
import com.kabunx.component.log.service.OperatorService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class BizLogInterceptor implements MethodInterceptor {

    private final BizLogAnnotationParser bizLogAnnotationParser = new BizLogAnnotationParser();

    private final BizLogExpressionParser bizLogExpressionParser;

    private final OperatorService operatorService;

    private final BizLogServiceHolder bizLogServiceHolder;

    private String tenantName;

    private boolean joinTransaction;

    public BizLogInterceptor(BizLogExpressionParser bizLogExpressionParser,
                             OperatorService operatorService,
                             BizLogServiceHolder bizLogServiceHolder) {
        this.bizLogExpressionParser = bizLogExpressionParser;
        this.operatorService = operatorService;
        this.bizLogServiceHolder = bizLogServiceHolder;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 代理不拦截
        if (AopUtils.isAopProxy(invocation.getThis())) {
            return invocation.proceed();
        }
        StopWatch stopWatch = new StopWatch(BizLogMonitor.MONITOR_NAME);
        stopWatch.start(BizLogMonitor.MONITOR_TASK_BEFORE_EXECUTE);
        MethodExecute methodExecute = new MethodExecute(invocation);
        BizLogContext.empty();
        Collection<BizLogExpression> expressions = new ArrayList<>();
        Map<String, String> beforeValueMap = new HashMap<>();
        try {
            expressions = bizLogAnnotationParser.getBizLogExpressions(methodExecute);
            List<String> spElTemplates = getSpElTemplatesBeforeExecute(expressions);
            beforeValueMap = bizLogExpressionParser.handleSpElTemplatesBeforeExecute(spElTemplates, methodExecute);
        } catch (Exception e) {
            log.error("log record parse before function exception", e);
        } finally {
            stopWatch.stop();
        }
        // 程序处理
        Object result = null;
        try {
            result = invocation.proceed();
            methodExecute.setSuccess(true);
            methodExecute.setResult(result);
        } catch (Exception e) {
            methodExecute.setSuccess(false);
            methodExecute.setThrowable(e);
            methodExecute.setErrorMsg(e.getMessage());
        }
        stopWatch.start(BizLogMonitor.MONITOR_TASK_AFTER_EXECUTE);
        try {
            if (!CollectionUtils.isEmpty(expressions)) {
                handleBizLogAfterExecute(methodExecute, beforeValueMap, expressions);
            }
        } catch (Exception t) {
            log.error("[BizLog] handle biz log exception", t);
            throw t;
        } finally {
            BizLogContext.clear();
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());
        }
        return result;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setJoinTransaction(boolean joinTransaction) {
        this.joinTransaction = joinTransaction;
    }

    /**
     * 将注解中的元素加入到解析的模板中（方法执行前）
     *
     * @param expressions 注解信息
     * @return 模板信息
     */
    private List<String> getSpElTemplatesBeforeExecute(Collection<BizLogExpression> expressions) {
        List<String> spElTemplates = new ArrayList<>();
        for (BizLogExpression expression : expressions) {
            List<String> templates = getSpElTemplates(expression, expression.getSuccess());
            if (!CollectionUtils.isEmpty(templates)) {
                spElTemplates.addAll(templates);
            }
        }
        return spElTemplates;
    }

    private void handleBizLogAfterExecute(MethodExecute methodExecute,
                                          Map<String, String> beforeValueMap,
                                          Collection<BizLogExpression> expressions) {
        for (BizLogExpression expression : expressions) {
            try {
                if (StringUtils.isEmpty(expression.getSuccess()) && StringUtils.isEmpty(expression.getError())) {
                    continue;
                }
                if (exitsCondition(methodExecute, beforeValueMap, expression)) {
                    continue;
                }
                if (methodExecute.isSuccess()) {
                    handleSuccessBizLog(methodExecute, beforeValueMap, expression);
                } else {
                    handleErrorBizLog(methodExecute, beforeValueMap, expression);
                }
            } catch (Exception t) {
                log.error("log record execute exception", t);
                if (joinTransaction) {
                    throw t;
                }
            }
        }
    }

    /**
     * 处理成功的模板日志
     */
    private void handleSuccessBizLog(MethodExecute execute, Map<String, String> beforeValueMap, BizLogExpression expression) {
        String detail;
        if (!StringUtils.isEmpty(expression.getSuccess())) {
            String condition = bizLogExpressionParser.parseTemplate(expression.getCondition(), execute, beforeValueMap);
            if (!StringUtils.endsWithIgnoreCase(condition, "true")) {
                return;
            }
            detail = expression.getSuccess();
        } else {
            detail = expression.getSuccess();
        }
        if (StringUtils.isEmpty(detail)) {
            // 没有日志内容则忽略
            return;
        }
        List<String> spElTemplates = getSpElTemplates(expression, detail);
        String operator = getOperatorAndPutTemplate(expression, spElTemplates);
        Map<String, String> expressionValues = bizLogExpressionParser.parseTemplates(spElTemplates, execute, beforeValueMap);
        saveLog(execute.getMethod(), "success", expression, operator, detail, expressionValues);
    }

    private void handleErrorBizLog(MethodExecute methodExecute, Map<String, String> beforeValueMap, BizLogExpression expression) {
        if (StringUtils.isEmpty(expression.getError())) {
            return;
        }
        String detail = expression.getError();
        List<String> spElTemplates = getSpElTemplates(expression, detail);
        String operatorIdFromService = getOperatorAndPutTemplate(expression, spElTemplates);

        Map<String, String> expressionValues = bizLogExpressionParser.parseTemplates(spElTemplates, methodExecute, beforeValueMap);
        saveLog(methodExecute.getMethod(), "error", expression, operatorIdFromService, detail, expressionValues);
    }

    private boolean exitsCondition(MethodExecute methodExecute, Map<String, String> beforeValueMap, BizLogExpression expression) {
        if (!StringUtils.isEmpty(expression.getCondition())) {
            String condition = bizLogExpressionParser.parseTemplate(expression.getCondition(), methodExecute, beforeValueMap);
            return StringUtils.endsWithIgnoreCase(condition, "false");
        }
        return false;
    }

    private void saveLog(Method method, String flag,
                         BizLogExpression expression, String operator,
                         String detail, Map<String, String> expressionValues) {
        if (StringUtils.isEmpty(expressionValues.get(detail))) {
            return;
        }
        if (Objects.equals(detail, expressionValues.get(detail))) {
            log.warn("模板没有被解析！");
        }
        BizLogEntity bizLogEntity = BizLogEntity.builder()
                .tenantName(tenantName)
                .type(expressionValues.get(expression.getType()))
                .bizNo(expressionValues.get(expression.getBizNo()))
                .operator(getRealOperator(expression, operator, expressionValues))
                .subType(expressionValues.get(expression.getSubType()))
                .extra(expressionValues.get(expression.getExtra()))
                .codeVariable(getCodeVariable(method))
                .detail(expressionValues.get(detail))
                .count(BizLogContext.getCountVariable())
                .flag(flag)
                .createTime(new Date())
                .build();
        // 保存
        bizLogServiceHolder.save(bizLogEntity);
    }

    private Map<CodeVariableType, Object> getCodeVariable(Method method) {
        Map<CodeVariableType, Object> map = new HashMap<>();
        map.put(CodeVariableType.ClassName, method.getDeclaringClass());
        map.put(CodeVariableType.MethodName, method.getName());
        return map;
    }

    private List<String> getSpElTemplates(BizLogExpression metadata, String... actions) {
        List<String> spElTemplates = new ArrayList<>();
        spElTemplates.add(metadata.getType());
        spElTemplates.add(metadata.getBizNo());
        spElTemplates.add(metadata.getSubType());
        spElTemplates.add(metadata.getExtra());
        spElTemplates.addAll(Arrays.asList(actions));
        return spElTemplates;
    }

    private String getRealOperator(BizLogExpression operation, String operator, Map<String, String> expressionValues) {
        return !StringUtils.isEmpty(operator) ? operator : expressionValues.get(operation.getOperator());
    }

    private String getOperatorAndPutTemplate(BizLogExpression expression, List<String> spElTemplates) {
        String realOperator = "";
        if (StringUtils.isEmpty(expression.getOperator())) {
            realOperator = operatorService.getCurrentAuth().getUsername();
            if (StringUtils.isEmpty(realOperator)) {
                throw new IllegalArgumentException("[BizLog] operator is null");
            }
        } else {
            spElTemplates.add(expression.getOperator());
        }
        return realOperator;
    }
}
