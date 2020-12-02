package com.dream.flink.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author fanrui03
 * @date 2020/9/20 15:39
 */
public class FlinkSqlUtil {

    public static StreamTableEnvironment getBlinkTableEnv(StreamExecutionEnvironment env) {
        EnvironmentSettings envSetting = EnvironmentSettings
            .newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build();

        return StreamTableEnvironment.create(env, envSetting);
    }

}
