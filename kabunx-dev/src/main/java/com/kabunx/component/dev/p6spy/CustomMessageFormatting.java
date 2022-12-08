package com.kabunx.component.dev.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;

public class CustomMessageFormatting implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotEmpty(sql)
                ? "SQL 耗时：" + elapsed + " ms " + now + "\n" + sql.replaceAll("[\\s]+", " ")
                : "";
    }
}
