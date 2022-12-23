package com.kabunx.component.log.service.impl;

import com.kabunx.component.log.dto.BizLogEntity;
import com.kabunx.component.log.service.BizLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultBizLogServiceImpl implements BizLogService {
    @Override
    public void save(BizLogEntity bizLogEntity) {
        log.info("[DefaultBizLog] - {}", bizLogEntity);
    }
}
