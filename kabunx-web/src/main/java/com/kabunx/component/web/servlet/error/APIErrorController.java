package com.kabunx.component.web.servlet.error;

import com.kabunx.component.common.dto.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Spring Boot 会将所有的异常发送到路径为 server.error.path（application.properties中可配置，默认为”/error”）的控制器方法中进行处理
 * 所以我们可以编写自定义的异常处理方法
 */
@Slf4j
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class APIErrorController extends AbstractErrorController {

    public APIErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping
    @ResponseStatus(HttpStatus.OK)
    public APIResponse<Object> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        log.error("[ErrorController] error status is {}", status);
        return APIResponse.failure(status.getReasonPhrase());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.OK)
    public APIResponse<Object> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        log.error("[ErrorController] error status is {}", status);
        return APIResponse.failure(status.getReasonPhrase());
    }
}
