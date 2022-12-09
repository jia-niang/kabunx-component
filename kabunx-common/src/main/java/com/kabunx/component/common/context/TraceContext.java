package com.kabunx.component.common.context;

import com.kabunx.component.common.constant.SecurityConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Schema(title = "追踪信息")
public class TraceContext implements Serializable {

    @Schema(title = "客户端", required = true)
    private String clientId;

    @Schema(title = "业务ID", required = true)
    private String bizId;

    @Schema(title = "链路追踪ID", required = true)
    private String traceId;

    /**
     * This is for extended values
     */
    @Schema(title = "扩展数据")
    protected Map<String, Object> extValues = new HashMap<>();


    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = StringUtils.isEmpty(clientId) ? SecurityConstants.UNKNOWN_CLIENT_ID : clientId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Object getExtField(String key) {
        if (extValues != null) {
            return extValues.get(key);
        }
        return null;
    }

    public void putExtField(String fieldName, Object value) {
        this.extValues.put(fieldName, value);
    }

    public Map<String, Object> getExtValues() {
        return extValues;
    }

    public void setExtValues(Map<String, Object> extValues) {
        this.extValues = extValues;
    }
}
