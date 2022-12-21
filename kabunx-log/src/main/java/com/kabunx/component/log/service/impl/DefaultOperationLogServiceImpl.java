package com.kabunx.component.log.service.impl;

import com.kabunx.component.log.dto.OperationLogEntity;
import com.kabunx.component.log.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultOperationLogServiceImpl implements OperationLogService {
    @Override
    public void save(OperationLogEntity operationLogEntity) {
        log.info("[DefaultLogRecord] log - {}", operationLogEntity);
    }
}
