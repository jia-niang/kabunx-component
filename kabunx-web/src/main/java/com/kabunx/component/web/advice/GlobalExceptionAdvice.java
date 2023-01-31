package com.kabunx.component.web.advice;

import com.kabunx.component.common.dto.RestResponse;
import com.kabunx.component.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = BizException.class)
    public RestResponse<Object> handle(BizException e) {
        log.error("[BizException] 业务异常", e);
        return RestResponse.failure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public RestResponse<Object> handleValidException(HttpMessageConversionException e) {
        log.error("[HttpMessageConversionException] 系统异常", e);
        return RestResponse.failure("信息序列化异常");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public RestResponse<Object> handleValidException(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] 系统异常", e);
        return RestResponse.failureWithErrors(getBindingErrors(e.getBindingResult()));
    }

    @ExceptionHandler(value = BindException.class)
    public RestResponse<Object> handleValidException(BindException e) {
        log.error("[BindException] 系统异常", e);
        return RestResponse.failureWithErrors(getBindingErrors(e.getBindingResult()));
    }

    @ExceptionHandler(value = Exception.class)
    public RestResponse<Object> handleException(Exception e) {
        log.error("[Exception] 系统异常", e);
        return RestResponse.failure("系统异常");
    }

    /**
     * 错误信息收集
     */
    public static HashMap<String, ArrayList<String>> getBindingErrors(BindingResult bindingResult) {
        HashMap<String, ArrayList<String>> errors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                ArrayList<String> fErrors = errors.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>());
                fErrors.add(fieldError.getDefaultMessage());
            }
        }
        return errors;
    }
}
