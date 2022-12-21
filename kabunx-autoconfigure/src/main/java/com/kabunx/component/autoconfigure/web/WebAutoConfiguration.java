package com.kabunx.component.autoconfigure.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabunx.component.web.advice.GlobalExceptionAdvice;
import com.kabunx.component.web.aspect.AdapterLogAspect;
import com.kabunx.component.web.context.SpringContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebAutoConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    /**
     * @return 适配层日志拦截器
     */
    @Bean
    @ConditionalOnClass(com.kabunx.component.web.aspect.AdapterLogAspect.class)
    public AdapterLogAspect adapterLogAspect() {
        return new AdapterLogAspect();
    }

    /**
     * @return 全局异常捕获拦截器
     */
    @Bean
    @ConditionalOnClass(com.kabunx.component.web.advice.GlobalExceptionAdvice.class)
    public GlobalExceptionAdvice globalExceptionAdvice() {
        return new GlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnClass(SpringContextHolder.class)
    public SpringContextHolder springContextUtils() {
        return new SpringContextHolder();
    }

}
