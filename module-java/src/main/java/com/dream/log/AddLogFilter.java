package com.dream.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.filter.BurstFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddLogFilter {

    protected static final Logger LOG = LoggerFactory.getLogger(AddLogFilter.class);

    public static void main(String[] args) throws InterruptedException {
        org.apache.logging.log4j.core.Logger rootLogger =
                (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        rootLogger.addFilter(BurstFilter.newBuilder().setMaxBurst(1).setRate(1).build());

        // 这个 filter 和上面的 filter 都会生效，所以小的生效
        org.apache.logging.log4j.core.Logger logFilterLogger =
                (org.apache.logging.log4j.core.Logger) LogManager.getLogger("com.dream.log.AddLogFilter");
        logFilterLogger.addFilter(BurstFilter.newBuilder().setMaxBurst(100).setRate(100).build());
        logFilterLogger.addFilter(new AbstractFilter() {
            @Override
            public Result filter(LogEvent event) {
                // logger name 会打印出类名
//                System.out.println(event.getLoggerName());
                return super.filter(event);
            }
        });

        // 打印所有的 logger 类型
//        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
//        Map<String, LoggerConfig> map = loggerContext.getConfiguration().getLoggers();
//
//        for (LoggerConfig loggerConfig : map.values()) {
//            System.out.println(loggerConfig);
//            String key = loggerConfig.getName();
//            loggerConfig.addFilter();
//        }
        for (int i = 0; i < 50; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            LOG.info("Info  test");
            LOG.warn("Info  test");
        }
    }
}
