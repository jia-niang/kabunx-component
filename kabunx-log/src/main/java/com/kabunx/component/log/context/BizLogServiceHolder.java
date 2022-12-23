package com.kabunx.component.log.context;

import com.kabunx.component.log.dto.BizLogEntity;
import com.kabunx.component.log.service.BizLogService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 支持业务日志多次处理
 */
public class BizLogServiceHolder implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private Map<String, BizLogService> bizLogServiceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        bizLogServiceMap = applicationContext.getBeansOfType(BizLogService.class);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 保存业务日志
     *
     * @param bizLogEntity 日志
     */
    public void save(BizLogEntity bizLogEntity) {
        for (BizLogService service : bizLogServiceMap.values()) {
            service.save(bizLogEntity);
        }
    }
}
