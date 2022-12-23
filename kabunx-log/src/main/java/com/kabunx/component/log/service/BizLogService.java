package com.kabunx.component.log.service;

import com.kabunx.component.log.dto.BizLogEntity;

public interface BizLogService {
    /**
     * 保存 log
     *
     * @param bizLogEntity 日志实体
     */
    void save(BizLogEntity bizLogEntity);
}
