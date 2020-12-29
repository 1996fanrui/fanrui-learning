package com.dream.flink.sql;

import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @author fanrui03
 * @date 2020/12/22 11:01
 */
public class FlinkSQLUtils {

    public static StreamTableEnvironment getTableEnv() {

        EnvironmentSettings mySetting = EnvironmentSettings
                .newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CheckpointUtil.setConfYamlStateBackend(env);

        return StreamTableEnvironment.create(env, mySetting);
    }
}
