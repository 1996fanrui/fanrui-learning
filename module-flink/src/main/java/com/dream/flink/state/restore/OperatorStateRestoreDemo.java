package com.dream.flink.state.restore;

import com.dream.flink.func.source.DataGenerator;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author fanrui03
 * @time 2020-05-24 14:49:14
 */
public class OperatorStateRestoreDemo {

    private static final String JOB_NAME = "hdp-operator-state-demo";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        CheckpointUtil.setFsStateBackend(env);

        env.addSource(new DataGenerator()).print();

        env.execute(JOB_NAME);
    }


}
