package com.kabunx.component.log.service.impl;

import com.kabunx.component.log.parser.FunctionParser;
import com.kabunx.component.log.parser.FunctionParserFactory;
import com.kabunx.component.log.service.FunctionService;

import java.util.Objects;

public class DefaultFunctionServiceImpl implements FunctionService {
    private final FunctionParserFactory functionParserFactory;

    public DefaultFunctionServiceImpl(FunctionParserFactory functionParserFactory) {
        this.functionParserFactory = functionParserFactory;
    }

    @Override
    public String apply(String functionName, String value) {
        FunctionParser function = functionParserFactory.getFunction(functionName);
        if (Objects.isNull(function)) {
            return value;
        }
        return function.apply(value);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return functionParserFactory.isBeforeFunction(functionName);
    }
}
