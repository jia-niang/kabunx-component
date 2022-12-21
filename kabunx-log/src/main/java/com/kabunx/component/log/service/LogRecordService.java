package com.kabunx.component.log.service;

import com.kabunx.component.log.dto.LogRecordEntity;

public interface LogRecordService {
    /**
     * 保存 log
     *
     * @param logRecordEntity 日志实体
     */
    void save(LogRecordEntity logRecordEntity);
}
