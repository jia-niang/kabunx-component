package com.kabunx.component.common.dto;

import com.kabunx.component.common.exception.BizErrorInfo;
import com.kabunx.component.common.exception.ErrorInfo;
import com.kabunx.component.common.util.JsonUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * Rest body to caller
 *
 * @param <T> 泛型数据
 */
@Schema(title = "REST风格返回体")
public class RestResponse<T> implements Serializable {
    @Schema(title = "是否成功", required = true)
    private boolean success;

    @Schema(title = "业务码，非状态码", required = true)
    private String code;

    @Schema(title = "提示信息", required = true)
    private String message;

    @Schema(title = "返回数据", required = true)
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static RestResponse<Object> empty() {
        return success();
    }

    public static <T> RestResponse<List<T>> list(List<T> data) {
        return success(data);
    }

    public static <T> RestResponse<T> success() {
        return success(null);
    }


    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> response = new RestResponse<>();
        response.setSuccess(true);
        response.setCode("000000");
        response.setMessage("请求成功");
        response.setData(data);
        return response;
    }

    public static <T> RestResponse<T> failure(String errCode, String errMessage) {
        RestResponse<T> response = new RestResponse<>();
        response.setSuccess(false);
        response.setCode(errCode);
        response.setMessage(errMessage);
        return response;
    }

    public static <T> RestResponse<T> failure(String errMessage) {
        return failure(BizErrorInfo.DEFAULT_ERROR.getCode(), errMessage);
    }

    public static <T> RestResponse<T> failureWithErrors(T errors) {
        RestResponse<T> response = new RestResponse<>();
        response.setSuccess(false);
        response.setCode(BizErrorInfo.VALIDATOR_ERROR.getCode());
        response.setMessage(BizErrorInfo.VALIDATOR_ERROR.getMessage());
        response.setData(errors);
        return response;
    }

    public static <T> RestResponse<T> failure(ErrorInfo error) {
        return failure(error.getCode(), error.getMessage());
    }

    public String toJson() {
        return JsonUtils.object2Json(this);
    }

    public byte[] toJsonBytes() {
        return JsonUtils.object2JsonBytes(this);
    }
}
