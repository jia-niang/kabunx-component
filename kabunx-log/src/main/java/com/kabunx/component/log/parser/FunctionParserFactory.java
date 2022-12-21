package com.kabunx.component.log.parser;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FunctionParserFactory {
    private Map<String, FunctionParser> allFunctionMap;

    public FunctionParserFactory(List<FunctionParser> functionParsers) {
        if (CollectionUtils.isEmpty(functionParsers)) {
            return;
        }
        allFunctionMap = new HashMap<>();
        for (FunctionParser functionParser : functionParsers) {
            if (StringUtils.isEmpty(functionParser.functionName())) {
                continue;
            }
            allFunctionMap.put(functionParser.functionName(), functionParser);
        }
    }

    public FunctionParser getFunction(String functionName) {
        return allFunctionMap.get(functionName);
    }

    public boolean isBeforeFunction(String functionName) {
        return Objects.nonNull(allFunctionMap.get(functionName)) && allFunctionMap.get(functionName).executeBefore();
    }
}
