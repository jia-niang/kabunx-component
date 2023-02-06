package com.kabunx.component.web.aop;

import com.kabunx.component.common.dto.APIResponse;
import com.kabunx.component.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(value = BizException.class)
    public APIResponse<Object> handle(BizException ex) {
        log.error("[BizException] 业务异常", ex);
        return APIResponse.failure(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public APIResponse<Object> handleValidException(HttpMessageConversionException ex) {
        log.error("[HttpMessageConversionException] 系统序列化异常", ex);
        return APIResponse.failure("信息序列化异常");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public APIResponse<Object> handleValidException(MethodArgumentNotValidException ex) {
        log.error("[MethodArgumentNotValidException] 参数异常", ex);
        return APIResponse.failureWithErrors(getBindingErrors(ex.getBindingResult()));
    }

    @ExceptionHandler(value = BindException.class)
    public APIResponse<Object> handleValidException(BindException ex) {
        log.error("[BindException] 参数异常", ex);
        return APIResponse.failureWithErrors(getBindingErrors(ex.getBindingResult()));
    }

    @ExceptionHandler
    public APIResponse<Object> handleException(HttpServletRequest request, HandlerMethod method, Exception ex) {
        log.error("[Exception] 访问 {} -> {} 出现系统异常", request.getRequestURI(), method.toString(), ex);
        return APIResponse.failure("服务器忙，请稍后再试");
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
