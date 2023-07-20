package com.dream.flink.optimize.deduplication;

import org.apache.flink.api.common.functions.RichFilterFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;

public class KeyedStateDeduplication {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        env.addSource(new IntegerRandomSource(10))
                .keyBy(record -> record)
                .filter(new DeduplicationFilter())
                .print();

        env.execute("KeyedStateDeduplication");
    }


    private static class IntegerRandomSource implements ParallelSourceFunction<Integer> {
        volatile boolean isRunning = true;
        private final int dataLength;

        private IntegerRandomSource(int dataLength) {
            this.dataLength = dataLength;
        }

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            RandomGenerator<Integer> generator = RandomGenerator.intGenerator(0, dataLength);
            generator.open(null, null, null);
            while (isRunning) {
                synchronized (ctx.getCheckpointLock()) {
                    ctx.collect(generator.next());
                }
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    public static class DeduplicationFilter extends RichFilterFunction<Integer> {
        // 使用该 ValueState 来标识当前 Key 是否之前存在过
        private ValueState<Boolean> isExist;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ValueStateDescriptor<Boolean> keyedStateDuplicated =
                    new ValueStateDescriptor<>("KeyedStateDeduplication",
                            TypeInformation.of(new TypeHint<Boolean>() {
                            }));
            // 状态 TTL 相关配置，过期时间设定为 1 分钟，根据业务语义进行调整。
            StateTtlConfig ttlConfig = StateTtlConfig
                    .newBuilder(Time.minutes(1))
                    .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                    .build();
            // 开启 TTL
            keyedStateDuplicated.enableTimeToLive(ttlConfig);
            // 从状态后端恢复状态
            isExist = getRuntimeContext().getState(keyedStateDuplicated);
        }

        @Override
        public boolean filter(Integer value) throws Exception {
            // 当前 key 第一次出现时，isExist.value() 会返回 null
            // key 第一次出现，说明当前 key 在之前没有被处理过，则不应该过滤
            if (null == isExist.value()) {
                isExist.update(true);
                return true;
            }

            // 如果 isExist.value() 不为 null，说明这条数据已经来过了，返回 false 过滤掉
            return false;
        }
    }
}
