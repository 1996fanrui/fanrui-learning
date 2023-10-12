package com.dream.flink.scheduler.failover;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Test failover delay for batch job.
 */
public class FailoverDelayBatchDemo {

    private static final Logger LOG = LoggerFactory.getLogger(FailoverDelayBatchDemo.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = getEnv();
        env.setRuntimeMode(RuntimeExecutionMode.BATCH);

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        300,
                        RateLimiterStrategy.perSecond(10),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .map((MapFunction<Long, Long>) value -> value).name("Map___1")
                .rebalance()
                .addSink(new MySink<>())
                .name("MySink");

        env.execute(FailoverDelayBatchDemo.class.getSimpleName());
    }

    public static class MySink<T> extends RichSinkFunction<T> {

        @Override
        public void open(Configuration parameters) throws Exception {
            System.out.println("open     subtaskIndex  " + getRuntimeContext().getIndexOfThisSubtask()
                    + "            timestamp " + System.currentTimeMillis());
        }

        @Override
        public void invoke(T value, Context context) throws Exception {
            Thread.sleep(1000);
            throw new RuntimeException("skhvkfsjihfjwhfknvs     subtaskIndex  " + getRuntimeContext().getIndexOfThisSubtask()
            + "            timestamp " + System.currentTimeMillis());
        }
    }

    private static StreamExecutionEnvironment getEnv() {
        String OSType = System.getProperty("os.name");
        LOG.info("start job on {}", OSType);

        Configuration conf = new Configuration();
        conf.set(JobManagerOptions.SCHEDULER, JobManagerOptions.SchedulerType.Default);
//        conf.setString("restart-strategy", "exponential-delay");
//        conf.setString("restart-strategy.exponential-delay.initial-backoff", "1s");

        // 5 个 subtask 每个 subtask 失败两次直接就变成 10 了。
        conf.setString("restart-strategy", "failure-rate");
        conf.setString("restart-strategy.failure-rate.delay", "1s");
        conf.setString("restart-strategy.failure-rate.failure-rate-interval", "1min");
        conf.setString("restart-strategy.failure-rate.max-failures-per-interval", "10");

        StreamExecutionEnvironment env = OSType.startsWith("Mac OS") ? getIdeaEnv(conf) : getProdEnv(conf);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(15), CheckpointingMode.EXACTLY_ONCE);
        return env;
    }

    private static StreamExecutionEnvironment getProdEnv(Configuration conf) {
        return StreamExecutionEnvironment.getExecutionEnvironment(conf);
    }

    private static StreamExecutionEnvironment getIdeaEnv(Configuration conf) {
        conf.setString("state.checkpoint-storage", "filesystem");
        conf.setString("state.checkpoints.dir", "file:///tmp/flinkjob");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(5);
        return env;
    }

}
