package com.kabunx.component.log.aop;

import com.kabunx.component.common.context.AuthContextHolder;
import com.kabunx.component.common.util.ThreadUtils;
import com.kabunx.component.log.context.BizLogServiceHolder;
import com.kabunx.component.log.dto.*;
import com.kabunx.component.log.BizLogMonitor;
import com.kabunx.component.log.parser.BizLogAnnotationParser;
import com.kabunx.component.log.context.BizLogContextHolder;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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
        MethodExecute methodExecute = new MethodExecute(invocation);
        BizLogContextHolder.empty();
        // 程序处理
        try {
            Object result = invocation.proceed();
            methodExecute.setSuccess(true);
            methodExecute.setResult(result);
            return result;
        } catch (Exception ex) {
            methodExecute.setSuccess(false);
            methodExecute.setThrowable(ex);
            methodExecute.setErrorMsg(ex.getMessage());
            throw ex;
        } finally {
            // 暂时没有考虑到更好的办法
            BizLogContextHolder.setVariable("auth", AuthContextHolder.getCurrentAuth());
            methodExecute.setContextVariables(BizLogContextHolder.getVariables());
            ExecutorService executor = ThreadUtils.getIoThreadPoolExecutor();
            executor.execute(() -> {
                try {
                    handleBizLogAfterExecute(methodExecute);
                } catch (Exception ex) {
                    log.error("[BizLog] handle biz log exception", ex);
                }
            });
            BizLogContextHolder.clear();
        }
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setJoinTransaction(boolean joinTransaction) {
        this.joinTransaction = joinTransaction;
    }

    private void handleBizLogAfterExecute(MethodExecute execute) {
        Collection<BizLogExpression> expressions = bizLogAnnotationParser.getBizLogExpressions(execute);
        if (CollectionUtils.isEmpty(expressions)) {
            return;
        }
        ExpressionArgs args = bizLogExpressionParser.getExpressionArgs(execute);
        for (BizLogExpression expression : expressions) {
            try {
                if (StringUtils.isEmpty(expression.getContent())) {
                    continue;
                }
                if (checkConditionIsFalse(expression, args)) {
                    continue;
                }
                if (execute.isSuccess()) {
                    handleSuccessBizLog(expression, args);
                } else {
                    handleErrorBizLog(expression, args);
                }
            } catch (Exception t) {
                log.error("[BizLog] log execute exception", t);
                if (joinTransaction) {
                    throw t;
                }
            }
        }
    }

    /**
     * 处理成功的模板日志
     */
    private void handleSuccessBizLog(BizLogExpression expression, ExpressionArgs args) {
        if (StringUtils.isEmpty(expression.getContent())) {
            return;
        }
        List<String> spElTemplates = getSpElTemplates(expression, expression.getContent());
        String operator = getOperatorAndPutTemplate(expression, spElTemplates);
        Map<String, String> expressionValues = bizLogExpressionParser.doParse(spElTemplates, args);
        saveLog(expression, operator, expression.getContent(), expressionValues);
    }

    private void handleErrorBizLog(BizLogExpression expression, ExpressionArgs args) {
        log.info("xx");
    }

    private boolean checkConditionIsFalse(BizLogExpression expression, ExpressionArgs args) {
        if (!StringUtils.isEmpty(expression.getCondition())) {
            String condition = bizLogExpressionParser.doParse(expression.getCondition(), args);
            return StringUtils.endsWithIgnoreCase(condition, "false");
        }
        return false;
    }

    private void saveLog(BizLogExpression expression, String operator,
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
                .detail(expressionValues.get(detail))
                .count(BizLogContextHolder.getCountVariable())
                .flag("success")
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
