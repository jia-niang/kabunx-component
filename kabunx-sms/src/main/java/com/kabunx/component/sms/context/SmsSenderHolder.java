package com.kabunx.component.sms.context;

import com.kabunx.component.sms.SmsSender;
import com.kabunx.component.sms.dto.SendSmsEvent;
import com.kabunx.component.sms.exception.SmsException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 各类短信发送器的持有者
 * 可执行具体的发送
 */
public class SmsSenderHolder implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    private final Map<String, SmsSender> handlers = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, SmsSender> senderMap = applicationContext.getBeansOfType(SmsSender.class);
        senderMap.forEach((name, sender) -> handlers.putIfAbsent(sender.getSenderName(), sender));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @param name 发送器名称
     * @return 具体的发送器
     */
    @Nullable
    public SmsSender getSmsSender(String name) {
        return handlers.get(name);
    }

    /**
     * 执行某个发送事件
     *
     * @param event 发送事件
     * @throws SmsException 异常
     */
    public void doSend(SendSmsEvent event) throws SmsException {
        SmsSender sender = getSmsSender(event.getSender().getName());
        if (Objects.isNull(sender)) {
            throw new SmsException("未定义该类型短息发送器");
        }
        sender.doSend(event);
    }
}
