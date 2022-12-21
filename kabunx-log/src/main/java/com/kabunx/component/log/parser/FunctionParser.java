package com.kabunx.component.log.parser;

public interface FunctionParser {
    default boolean executeBefore() {
        return false;
    }

    String functionName();

    String apply(String value);
}
