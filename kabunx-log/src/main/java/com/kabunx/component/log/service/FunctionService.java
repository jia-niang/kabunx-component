package com.kabunx.component.log.service;

public interface FunctionService {

    String apply(String functionName, String value);

    boolean beforeFunction(String functionName);
}
