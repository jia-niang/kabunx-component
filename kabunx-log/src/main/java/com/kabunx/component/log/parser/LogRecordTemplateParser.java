package com.kabunx.component.log.parser;

import com.kabunx.component.log.context.FunctionTemplateHolder;
import com.kabunx.component.log.dto.MethodExecuteResult;
import com.kabunx.component.log.context.expression.LogRecordExpressionEvaluator;
import com.kabunx.component.log.util.DiffUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogRecordTemplateParser extends FunctionTemplateParser implements BeanFactoryAware {

    private static final Pattern pattern = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");
    public static final String COMMA = ",";
    protected BeanFactory beanFactory;

    private final LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();

    public LogRecordTemplateParser(FunctionTemplateHolder functionTemplateHolder) {
        super(functionTemplateHolder);
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static int textCount(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public String singleProcessTemplate(MethodExecuteResult methodExecuteResult,
                                        String templates,
                                        Map<String, String> beforeFunctionNameAndReturnMap) {
        Map<String, String> stringStringMap = processTemplate(Collections.singletonList(templates), methodExecuteResult,
                beforeFunctionNameAndReturnMap);
        return stringStringMap.get(templates);
    }

    public Map<String, String> processTemplate(Collection<String> templates,
                                               MethodExecuteResult methodExecuteResult,
                                               Map<String, String> beforeFunctionNameAndReturnMap) {
        Map<String, String> expressionValues = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(methodExecuteResult.getMethod(),
                methodExecuteResult.getArgs(), methodExecuteResult.getTargetClass(), methodExecuteResult.getResult(),
                methodExecuteResult.getErrorMsg(), beanFactory);

        for (String template : templates) {
            if (StringUtils.isEmpty(template) || !template.contains("{")) {
                expressionValues.put(template, template);
                continue;
            }
            Matcher matcher = pattern.matcher(template);
            StringBuffer parsedStr = new StringBuffer();
            AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(methodExecuteResult.getMethod(), methodExecuteResult.getTargetClass());
            boolean flag = true;
            while (matcher.find()) {
                String expression = matcher.group(2);
                String functionName = matcher.group(1);
                if (DiffUtils.diffFunctionName.equals(functionName)) {
                    expression = getDiffFunctionValue(evaluationContext, annotatedElementKey, expression);
                } else {
                    Object value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    expression = getFunctionReturnValue(beforeFunctionNameAndReturnMap, value, expression, functionName);
                }
                if (expression != null && !Objects.equals(expression, "")) {
                    flag = false;
                }
                matcher.appendReplacement(parsedStr, Matcher.quoteReplacement(expression == null ? "" : expression));
            }
            matcher.appendTail(parsedStr);
            expressionValues.put(template, flag ? template : parsedStr.toString());
        }
        return expressionValues;
    }

    private String getDiffFunctionValue(EvaluationContext evaluationContext, AnnotatedElementKey annotatedElementKey, String expression) {
        String[] params = parseDiffFunction(expression);
        if (params.length == 1) {
            Object targetObj = expressionEvaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            expression = DiffUtils.diff(targetObj);
        } else if (params.length == 2) {
            Object sourceObj = expressionEvaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            Object targetObj = expressionEvaluator.parseExpression(params[1], annotatedElementKey, evaluationContext);
            expression = DiffUtils.diff(sourceObj, targetObj);
        }
        return expression;
    }

    private String[] parseDiffFunction(String expression) {
        if (expression.contains(COMMA) && textCount(expression, COMMA) == 1) {
            return expression.split(COMMA);
        }
        return new String[]{expression};
    }

    public Map<String, String> processBeforeExecuteFunctionTemplate(Collection<String> templates, Class<?> targetClass, Method method, Object[] args) {
        Map<String, String> functionNameAndReturnValueMap = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);
        for (String template : templates) {
            if (StringUtils.isEmpty(template) || !template.contains("{")) {
                continue;
            }
            Matcher matcher = pattern.matcher(template);
            while (matcher.find()) {
                String expression = matcher.group(2);
                if (expression.contains("#_result") || expression.contains("#_errorMsg")) {
                    continue;
                }
                AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                String functionName = matcher.group(1);
                if (isBeforeFunction(functionName)) {
                    Object value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    String functionReturnValue = getFunctionReturnValue(null, value, expression, functionName);
                    String functionCallInstanceKey = getFunctionCallInstanceKey(functionName, expression);
                    functionNameAndReturnValueMap.put(functionCallInstanceKey, functionReturnValue);
                }
            }
        }
        return functionNameAndReturnValueMap;
    }
}
