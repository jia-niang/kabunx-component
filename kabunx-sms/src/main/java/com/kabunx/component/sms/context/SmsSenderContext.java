package com.kabunx.component.sms.context;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.dto.SmsSendEvent;
import com.kabunx.component.sms.exception.SmsException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SmsSenderContext implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    private final Map<String, SmsSender> handlers = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, SmsSender> handlerMap = applicationContext.getBeansOfType(SmsSender.class);
        handlerMap.forEach((name, handler) -> handlers.putIfAbsent(handler.getSenderName(), handler));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @param name 处理器名称
     * @return 具体的实现
     */
    @Nullable
    public SmsSender getSmsSender(String name) {
        return handlers.get(name);
    }

    public void doSend(SmsSendEvent event) throws SmsException {
        SmsSender sender = getSmsSender(event.getSenderName());
        if (Objects.isNull(sender)) {
            throw new SmsException("未定义该类型短息发送器");
        }
        sender.doSend(event);
    }
}
