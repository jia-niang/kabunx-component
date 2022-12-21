package com.kabunx.component.log.parser;

import com.kabunx.component.log.FunctionTemplate;
import com.kabunx.component.log.context.FunctionTemplateHolder;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

public class FunctionTemplateParser {
    private final FunctionTemplateHolder functionTemplateHolder;

    public FunctionTemplateParser(FunctionTemplateHolder functionTemplateHolder) {
        this.functionTemplateHolder = functionTemplateHolder;
    }

    public String getFunctionReturnValue(Map<String, String> beforeFunctionNameAndReturnMap, Object value, String expression, String functionName) {
        if (StringUtils.isEmpty(functionName)) {
            return Objects.isNull(value) ? "" : value.toString();
        }
        String functionReturnValue;
        String functionCallInstanceKey = getFunctionCallInstanceKey(functionName, expression);
        if (beforeFunctionNameAndReturnMap != null && beforeFunctionNameAndReturnMap.containsKey(functionCallInstanceKey)) {
            functionReturnValue = beforeFunctionNameAndReturnMap.get(functionCallInstanceKey);
        } else {
            functionReturnValue = apply(functionName, (String) value);
        }
        return functionReturnValue;
    }

    /**
     * @param functionName    函数名称
     * @param paramExpression 解析前的表达式
     * @return 函数缓存的key
     * 方法执行之前换成函数的结果，此时函数调用的唯一标志：函数名+参数表达式
     */
    public String getFunctionCallInstanceKey(String functionName, String paramExpression) {
        return functionName + paramExpression;
    }

    public boolean isBeforeFunction(String functionName) {
        return functionTemplateHolder.isBeforeFunction(functionName);
    }

    public String apply(String functionName, String value) {
        FunctionTemplate function = functionTemplateHolder.getFunctionTemplate(functionName);
        if (Objects.isNull(function)) {
            return value;
        }
        return function.apply(value);
    }
}
