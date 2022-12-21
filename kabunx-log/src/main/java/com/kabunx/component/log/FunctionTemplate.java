package com.kabunx.component.log;

public interface FunctionTemplate {
    default boolean executeBefore() {
        return false;
    }

    String functionName();

    String apply(String value);
}
