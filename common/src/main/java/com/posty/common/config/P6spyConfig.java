package com.posty.common.config;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6spyConfig {

    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance()
                .setLogMessageFormat(PrettyFormat.class.getName());
    }

    public static class PrettyFormat implements MessageFormattingStrategy {

        @Override
        public String formatMessage(int connectionId, String now, long elapsed,
                                    String category, String prepared, String sql, String url) {
            if (sql == null || sql.isBlank()) {
                return "";
            }
            return String.format("\n\t[%s] | %dms | %s\n", category, elapsed, sql.trim());
        }
    }
}
