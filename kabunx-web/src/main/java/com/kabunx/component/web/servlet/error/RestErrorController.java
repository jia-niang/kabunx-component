package com.kabunx.component.web.servlet.error;

import com.kabunx.component.common.dto.RestResponse;
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

@Slf4j
@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class RestErrorController extends AbstractErrorController {

    public RestErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    @RequestMapping
    @ResponseStatus(HttpStatus.OK)
    public RestResponse<Object> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        log.error("[ErrorController] error status is {}", status);
        return RestResponse.failure(status.getReasonPhrase());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResponse<Object> mediaTypeNotAcceptable(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        log.error("[ErrorController] error status is {}", status);
        return RestResponse.failure(status.getReasonPhrase());
    }
}
