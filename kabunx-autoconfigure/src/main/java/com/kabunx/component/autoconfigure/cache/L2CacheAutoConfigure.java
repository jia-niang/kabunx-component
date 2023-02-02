package com.kabunx.component.autoconfigure.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public class L2CacheAutoConfigure {

    private final MessageListenerAdapter listenerAdapter;

    public L2CacheAutoConfigure(MessageListenerAdapter listenerAdapter) {
        this.listenerAdapter = listenerAdapter;
    }

    /**
     * 消息监听容器
     *
     * @param factory RedisConnectionFactory
     * @return RedisMessageListenerContainer
     */
    @Bean
    RedisMessageListenerContainer listenerContainer(final RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listenerAdapter, new ChannelTopic("/cache/change"));
        return container;
    }
}
