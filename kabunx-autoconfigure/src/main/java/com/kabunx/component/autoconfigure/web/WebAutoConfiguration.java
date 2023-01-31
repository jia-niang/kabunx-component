package com.kabunx.component.autoconfigure.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kabunx.component.web.advice.GlobalExceptionAdvice;
import com.kabunx.component.web.aspect.AdapterLogAspect;
import com.kabunx.component.web.context.SpringContextHolder;
import com.kabunx.component.web.servlet.error.RestErrorController;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
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
    @ConditionalOnClass(AdapterLogAspect.class)
    public AdapterLogAspect adapterLogAspect() {
        return new AdapterLogAspect();
    }

    @Bean
    @ConditionalOnClass(RestErrorController.class)
    public RestErrorController restErrorController(ErrorAttributes errorAttributes,
                                                   ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new RestErrorController(errorAttributes, errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    /**
     * @return 全局异常捕获拦截器
     */
    @Bean
    @ConditionalOnClass(GlobalExceptionAdvice.class)
    public GlobalExceptionAdvice globalExceptionAdvice() {
        return new GlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnClass(SpringContextHolder.class)
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

}
