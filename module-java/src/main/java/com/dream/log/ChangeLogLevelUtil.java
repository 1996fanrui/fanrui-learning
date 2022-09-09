package com.dream.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ChangeLogLevelUtil {

    protected static final Logger LOG = LoggerFactory.getLogger(ChangeLogLevelUtil.class);

    private enum LogFrameworkType {
        LOG4J,
        LOGBACK,
        LOG4J2,
        UNKNOWN
    }

    private interface LogConstant {
        // todo, check all
        String LOG4J_LOGGER_FACTORY = "org.apache.log4j.Log4jLoggerFactory";
        String LOGBACK_LOGGER_FACTORY = "ch.qos.logback.classic.util.ContextSelectorStaticBinder";
        String LOG4J2_LOGGER_FACTORY = "org.apache.logging.slf4j.Log4jLoggerFactory";
    }

    private static LogFrameworkType logFrameworkType;
    private static final Map<String, Object> loggerMap = new HashMap<>();

    public static void init() {
        String type = StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr();
        LOG.info("Log type={}", type);
        if (LogConstant.LOG4J_LOGGER_FACTORY.equals(type)) {
            //         <dependency>
            //            <groupId>org.slf4j</groupId>
            //            <artifactId>slf4j-api</artifactId>
            //            <version>1.6.6</version>
            //        </dependency>
            //        <dependency>
            //            <groupId>org.slf4j</groupId>
            //            <artifactId>log4j-over-slf4j</artifactId>
            //            <version>1.7.25</version>
            //        </dependency>

            logFrameworkType = LogFrameworkType.LOG4J;
            Enumeration enumeration = org.apache.log4j.LogManager.getCurrentLoggers();
            while (enumeration.hasMoreElements()) {
                org.apache.log4j.Logger logger = (org.apache.log4j.Logger) enumeration.nextElement();
                if (logger.getLevel() != null) {
                    loggerMap.put(logger.getName(), logger);
                }
            }
            org.apache.log4j.Logger rootLogger = org.apache.log4j.LogManager.getRootLogger();
            loggerMap.put(rootLogger.getName(), rootLogger);
        } else if (LogConstant.LOGBACK_LOGGER_FACTORY.equals(type)) {
            //         <dependency>
            //            <groupId>ch.qos.logback</groupId>
            //            <artifactId>logback-core</artifactId>
            //            <version>1.2.3</version>
            //        </dependency>
            //        <dependency>
            //            <groupId>ch.qos.logback</groupId>
            //            <artifactId>logback-classic</artifactId>
            //            <version>1.2.3</version>
            //        </dependency>
            logFrameworkType = LogFrameworkType.LOGBACK;
//            ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
//            for (ch.qos.logback.classic.Logger logger : loggerContext.getLoggerList()) {
//                if (logger.getLevel() != null) {
//                    loggerMap.put(logger.getName(), logger);
//                }
//            }
//            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//            loggerMap.put(rootLogger.getName(), rootLogger);
        } else if (LogConstant.LOG4J2_LOGGER_FACTORY.equals(type)) {
            //        <dependency>
            //            <groupId>org.apache.logging.log4j</groupId>
            //            <artifactId>log4j-core</artifactId>
            //            <version>2.6.2</version>
            //        </dependency>
            //
            //        <dependency>
            //            <groupId>org.apache.logging.log4j</groupId>
            //            <artifactId>log4j-api</artifactId>
            //            <version>2.6.2</version>
            //        </dependency>
            logFrameworkType = LogFrameworkType.LOG4J2;
            org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
            Map<String, org.apache.logging.log4j.core.config.LoggerConfig> map = loggerContext.getConfiguration().getLoggers();
            for (org.apache.logging.log4j.core.config.LoggerConfig loggerConfig : map.values()) {
                String key = loggerConfig.getName();
                if (key == null || "".equals(key.trim())) {
                    key = "root";
                }
                loggerMap.put(key, loggerConfig);
            }
        } else {
            logFrameworkType = LogFrameworkType.UNKNOWN;
            LOG.error("Log框架无法识别: type={}", type);
        }
    }

    public static String setLogLevel(String loggerLevel) {
        for (Object logger : loggerMap.values()) {
            if (logger == null) {
                throw new RuntimeException("需要修改日志级别的Logger不存在");
            }
            if (logFrameworkType == LogFrameworkType.LOG4J) {
                org.apache.log4j.Logger targetLogger = (org.apache.log4j.Logger) logger;
                org.apache.log4j.Level targetLevel = org.apache.log4j.Level.toLevel(loggerLevel);
                targetLogger.setLevel(targetLevel);
            } else if (logFrameworkType == LogFrameworkType.LOGBACK) {
//                ch.qos.logback.classic.Logger targetLogger = (ch.qos.logback.classic.Logger) logger;
//                ch.qos.logback.classic.Level targetLevel = ch.qos.logback.classic.Level.toLevel(loggerLevel);
//                targetLogger.setLevel(targetLevel);
            } else if (logFrameworkType == LogFrameworkType.LOG4J2) {
                org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = (org.apache.logging.log4j.core.config.LoggerConfig) logger;
                org.apache.logging.log4j.Level targetLevel = org.apache.logging.log4j.Level.toLevel(loggerLevel);
                loggerConfig.setLevel(targetLevel);
                org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
                ctx.updateLoggers(); // This causes all Loggers to refetch information from their LoggerConfig.
            } else {
                throw new RuntimeException("Logger的类型未知,无法处理!");
            }
        }
        return "success";
    }
}
