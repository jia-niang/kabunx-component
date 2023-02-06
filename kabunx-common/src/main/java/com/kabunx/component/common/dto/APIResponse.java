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
@Schema(title = "API返回体")
public class APIResponse<T> implements Serializable {
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

    public static APIResponse<Object> empty() {
        return success();
    }

    public static <T> APIResponse<List<T>> list(List<T> data) {
        return success(data);
    }

    public static <T> APIResponse<T> success() {
        return success(null);
    }


    public static <T> APIResponse<T> success(T data) {
        APIResponse<T> response = new APIResponse<>();
        response.setSuccess(true);
        response.setCode("000000");
        response.setMessage("请求成功");
        response.setData(data);
        return response;
    }

    public static <T> APIResponse<T> failure(String errCode, String errMessage) {
        APIResponse<T> response = new APIResponse<>();
        response.setSuccess(false);
        response.setCode(errCode);
        response.setMessage(errMessage);
        return response;
    }

    public static <T> APIResponse<T> failure(String errMessage) {
        return failure(BizErrorInfo.DEFAULT_ERROR.getCode(), errMessage);
    }

    public static <T> APIResponse<T> failureWithErrors(T errors) {
        APIResponse<T> response = new APIResponse<>();
        response.setSuccess(false);
        response.setCode(BizErrorInfo.VALIDATOR_ERROR.getCode());
        response.setMessage(BizErrorInfo.VALIDATOR_ERROR.getMessage());
        response.setData(errors);
        return response;
    }

    public static <T> APIResponse<T> failure(ErrorInfo error) {
        return failure(error.getCode(), error.getMessage());
    }

    public String toJson() {
        return JsonUtils.object2Json(this);
    }

    public byte[] toJsonBytes() {
        return JsonUtils.object2JsonBytes(this);
    }
}
