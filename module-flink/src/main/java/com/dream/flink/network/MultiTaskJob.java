package com.dream.flink.network;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FLINK-22643 test job
 * 1. Test whether there are many connections when there are many tasks. Yes
 * 2. Test the performance of the job when taskmanager.network.max-num-tcp-connections is different.
 */
public class MultiTaskJob {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int parallelism = parameterTool.getInt("parallelism", 10);
        int taskNumber = parameterTool.getInt("task_number", 10);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(parallelism);

        DataStream<Tuple3<String, Integer, Long>> stream = env.addSource(new RichParallelSourceFunction<Tuple3<String, Integer, Long>>() {
            private volatile boolean isRunning = true;
            private int subtaskIndex;
            private AtomicLong counter;
            private final String TEST_STR = "fjsokdfjlsjflkjslknnvmcnmxnkjsfkshfsknvnsknfsfnksnknsnvnxnvmxnvnj";

            @Override
            public void open(Configuration parameters) throws Exception {
                this.subtaskIndex = getRuntimeContext().getIndexOfThisSubtask();
                this.counter = new AtomicLong();
            }

            @Override
            public void run(SourceContext<Tuple3<String, Integer, Long>> ctx) throws Exception {
                while (isRunning) {
                    ctx.collect(Tuple3.of(TEST_STR, subtaskIndex, counter.getAndIncrement()));
                }
            }

            @Override
            public void cancel() {
                this.isRunning = false;
            }
        }).rebalance();

        for (int i = 1; i < taskNumber; i++) {
            stream = stream.map(new MapFunction<Tuple3<String, Integer, Long>, Tuple3<String, Integer, Long>>() {
                @Override
                public Tuple3<String, Integer, Long> map(Tuple3<String, Integer, Long> value) throws Exception {
                    return value;
                }
            }).rebalance();
        }

        env.execute(MultiTaskJob.class.getSimpleName());
    }
}
