package com.dream.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * ref: https://tech.meituan.com/2017/02/17/change-log-level.html
 */
public class DynamicModifyLogLevel {
    protected static final Logger LOG = LoggerFactory.getLogger(DynamicModifyLogLevel.class);

    public static void main(String[] args) throws InterruptedException {
        ChangeLogLevelUtil.init();

        int count = 0;
        while (count < 10) {
            LOG.info("info log");
            LOG.debug("debug log");
            TimeUnit.SECONDS.sleep(1);
            count++;
            if (count == 5) {
                ChangeLogLevelUtil.setLogLevel("DEBUG");
            }
        }
    }
}
