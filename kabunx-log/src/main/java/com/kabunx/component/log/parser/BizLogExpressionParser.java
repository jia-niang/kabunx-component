package com.kabunx.component.log.parser;

import com.kabunx.component.log.context.FunctionTemplateHolder;
import com.kabunx.component.log.dto.MethodExecute;
import com.kabunx.component.log.context.expression.BizLogExpressionEvaluator;
import com.kabunx.component.log.util.DiffUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 包装参数发给{@link BizLogExpressionEvaluator}解析的工具类
 */
public class BizLogExpressionParser extends FunctionExpressionParser implements BeanFactoryAware {

    private static final Pattern pattern = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");
    public static final String COMMA = ",";

    private final BizLogExpressionEvaluator expressionEvaluator = new BizLogExpressionEvaluator();

    protected BeanFactory beanFactory;

    public BizLogExpressionParser(FunctionTemplateHolder functionTemplateHolder) {
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

    public String parseTemplate(String template, MethodExecute execute, Map<String, String> beforeValues) {
        Map<String, String> stringMap = parseTemplates(Collections.singletonList(template), execute, beforeValues);
        return stringMap.get(template);
    }

    public Map<String, String> parseTemplates(Collection<String> templates, MethodExecute execute, Map<String, String> beforeValues) {
        Map<String, String> expressionValues = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.buildEvaluationContext(execute, beanFactory);
        for (String template : templates) {
            if (StringUtils.isEmpty(template) || !template.contains("{")) {
                expressionValues.put(template, template);
                continue;
            }
            Matcher matcher = pattern.matcher(template);
            StringBuffer parsedStr = new StringBuffer();
            AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(execute.getMethod(), execute.getTargetClass());
            boolean flag = true;
            while (matcher.find()) {
                String expression = matcher.group(2);
                String functionName = matcher.group(1);
                if (DiffUtils.diffFunctionName.equals(functionName)) {
                    expression = getDiffFunctionValue(evaluationContext, annotatedElementKey, expression);
                } else {
                    Object value = expressionEvaluator.parse(expression, annotatedElementKey, evaluationContext);
                    expression = getFunctionReturnValue(beforeValues, value, expression, functionName);
                }
                if (!StringUtils.isEmpty(expression)) {
                    flag = false;
                }
                matcher.appendReplacement(parsedStr, Matcher.quoteReplacement(Objects.isNull(expression) ? "" : expression));
            }
            matcher.appendTail(parsedStr);
            expressionValues.put(template, flag ? template : parsedStr.toString());
        }
        return expressionValues;
    }

    private String getDiffFunctionValue(EvaluationContext evaluationContext, AnnotatedElementKey annotatedElementKey, String expression) {
        String[] params = parseDiffFunction(expression);
        if (params.length == 1) {
            Object targetObj = expressionEvaluator.parse(params[0], annotatedElementKey, evaluationContext);
            expression = DiffUtils.diff(targetObj);
        } else if (params.length == 2) {
            Object sourceObj = expressionEvaluator.parse(params[0], annotatedElementKey, evaluationContext);
            Object targetObj = expressionEvaluator.parse(params[1], annotatedElementKey, evaluationContext);
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

    public Map<String, String> handleSpElTemplatesBeforeExecute(Collection<String> templates, MethodExecute execute) {
        Map<String, String> beforeValueMap = new HashMap<>();
        EvaluationContext evaluationContext = expressionEvaluator.buildEvaluationContext(execute, beanFactory);
        for (String template : templates) {
            if (StringUtils.isEmpty(template) || !template.contains("{")) {
                continue;
            }
            Matcher matcher = pattern.matcher(template);
            while (matcher.find()) {
                String expression = matcher.group(2);
                if (expression.contains("#result") || expression.contains("#errorMsg")) {
                    continue;
                }
                AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(execute.getMethod(), execute.getTargetClass());
                String functionName = matcher.group(1);
                if (isBeforeFunction(functionName)) {
                    Object value = expressionEvaluator.parse(expression, annotatedElementKey, evaluationContext);
                    String functionReturnValue = getFunctionReturnValue(null, value, expression, functionName);
                    String functionCallInstanceKey = getFunctionCallInstanceKey(functionName, expression);
                    beforeValueMap.put(functionCallInstanceKey, functionReturnValue);
                }
            }
        }
        return beforeValueMap;
    }
}
