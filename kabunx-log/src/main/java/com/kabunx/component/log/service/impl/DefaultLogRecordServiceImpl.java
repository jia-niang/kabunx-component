package com.kabunx.component.log.service.impl;

import com.kabunx.component.log.dto.LogRecordEntity;
import com.kabunx.component.log.service.LogRecordService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLogRecordServiceImpl implements LogRecordService {
    @Override
    public void save(LogRecordEntity logRecordEntity) {
        log.info("[DefaultLogRecord] log - {}", logRecordEntity);
    }
}
