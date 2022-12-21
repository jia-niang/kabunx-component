package com.kabunx.component.log.service.impl;

import com.kabunx.component.common.context.AuthContext;
import com.kabunx.component.common.context.AuthContextHolder;
import com.kabunx.component.log.service.OperatorService;

public class DefaultOperatorServiceImpl implements OperatorService {
    @Override
    public AuthContext getCurrentAuth() {
        return AuthContextHolder.getCurrentAuth();
    }
}
