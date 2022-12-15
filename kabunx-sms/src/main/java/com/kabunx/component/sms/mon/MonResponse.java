package com.kabunx.component.sms.mon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class MonResponse implements Serializable {

    private static final Map<Integer, String> ERRORS = new HashMap<>();

    static {
        ERRORS.put(0, "请求成功");
        ERRORS.put(-100001, "鉴权未通过");
        ERRORS.put(-100002, "多次鉴权未通过");
        ERRORS.put(-100003, "用户欠费");
        ERRORS.put(-100004, "自定义字段不合法");
        ERRORS.put(-100011, "短信内容超长");
        ERRORS.put(-100012, "手机号码不合法");
        ERRORS.put(-100014, "手机号码超过最大支持数量（1000）");
        ERRORS.put(-100029, "端口绑定失败");
        ERRORS.put(-100056, "用户账号登录的连接数超限");
        ERRORS.put(-100057, "用户账号登录的IP错误");
        ERRORS.put(-100126, "短信有效存活时间无效");
        ERRORS.put(-100252, "业务类型不合法（超长或包含非字母数字字符）");
        ERRORS.put(-100253, "自定义参数超长");
        ERRORS.put(-100999, "梦网平台数据库内部错误");
    }

    private Integer result;
    @Schema(title = "平台流水号")
    private Long msgid;
    private String custid;
    private String exdata;

    public boolean success() {
        return result == 0;
    }

    public String getMessage() {
        return ERRORS.get(result);
    }

    public void doLog() {
        if (success()) {
            log.info("Mon msgId is {}, msg is {}", msgid, getMessage());
        } else {
            log.error("Mon msgId is {}, msg is {}", msgid, getMessage());
        }
    }
}
