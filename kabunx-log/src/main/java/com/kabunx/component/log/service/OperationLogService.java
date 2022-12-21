package com.kabunx.component.log.service;

import com.kabunx.component.log.dto.OperationLogEntity;

public interface OperationLogService {
    /**
     * 保存 log
     *
     * @param operationLogEntity 日志实体
     */
    void save(OperationLogEntity operationLogEntity);
}
