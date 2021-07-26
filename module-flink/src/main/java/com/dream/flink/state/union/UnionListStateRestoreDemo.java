package com.dream.flink.state.union;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.runtime.TupleSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class UnionListStateRestoreDemo {

    private static final Logger LOG = LoggerFactory.getLogger(UnionListStateRestoreDemo.class);

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        int elementCount = 100;
        if (args != null && args.length > 0) {
            elementCount = Integer.parseInt(args[0]);
        }
        env.addSource(new StatefulSource(elementCount)).filter(a -> a == 0);

        env.execute(UnionListStateRestoreDemo.class.getSimpleName());
    }

    private static class StatefulSource extends RichParallelSourceFunction<Integer>
            implements CheckpointedFunction {

        private static final String OFFSETS_STATE_NAME = "topic-partition-offset-states";

        private ListState<Tuple3<KafkaTopicPartition, Long, Long>> unionOffsetStates;

        private ListState<Tuple3<KafkaTopicPartition, Long, Long>> listStates;

        private volatile boolean running = true;

        private final int elementCount;

        private StatefulSource(int elementCount) {
            this.elementCount = elementCount;
        }

        @Override
        public void run(SourceContext<Integer> out) throws Exception {
            Random random = new Random();
            while (running) {
                TimeUnit.MILLISECONDS.sleep(10);
                out.collect(random.nextInt());
            }
            // 1. gc 严重
            // 2. cpu 不够用
            // 3. fs 慢，导致单线程超时
        }

        @Override
        public void cancel() {
            this.running = false;
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            this.unionOffsetStates.clear();
            this.listStates.clear();
            int index = getRuntimeContext().getIndexOfThisSubtask();
            Tuple3<KafkaTopicPartition, Long, Long> tuple3 = Tuple3.of(
                    new KafkaTopicPartition("merge_union_state_", index),
                    (long) index, context.getCheckpointId());
            this.unionOffsetStates.add(tuple3);
            LOG.info("snapshotState tuple3 : {}", tuple3);

            for (int i = 0; i < elementCount; i++) {
                this.listStates.add(tuple3);
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            OperatorStateStore stateStore = context.getOperatorStateStore();
            this.unionOffsetStates = stateStore.getUnionListState(
                    new ListStateDescriptor<>(
                            OFFSETS_STATE_NAME,
                            createStateSerializer(getRuntimeContext().getExecutionConfig())));

            this.listStates = stateStore.getListState(
                    new ListStateDescriptor<>(
                            "ordinary-list-state",
                            createStateSerializer(getRuntimeContext().getExecutionConfig())));

            if (context.isRestored()) {
                TreeMap<KafkaTopicPartition, Long> restoredState = new TreeMap<>(new KafkaTopicPartition.Comparator());

                long checkpointId = Long.MAX_VALUE;
                // populate actual holder for restored state
                for (Tuple3<KafkaTopicPartition, Long, Long> kafkaOffset : unionOffsetStates.get()) {
                    restoredState.put(kafkaOffset.f0, kafkaOffset.f1);
                    if (checkpointId == Long.MAX_VALUE) {
                        checkpointId = kafkaOffset.f2;
                        LOG.info("CheckpointId : {}", checkpointId);
                    }
                    Preconditions.checkState(checkpointId == kafkaOffset.f2);
                }

                LOG.info("Consumer subtask {} restored size: {}, restored state: {}.",
                        getRuntimeContext().getIndexOfThisSubtask(),
                        restoredState.size(), restoredState);
            } else {
                LOG.info("Consumer subtask {} has no restore state.",
                        getRuntimeContext().getIndexOfThisSubtask());
            }
        }

        static TupleSerializer<Tuple3<KafkaTopicPartition, Long, Long>> createStateSerializer(
                ExecutionConfig executionConfig) {
            // explicit serializer will keep the compatibility with GenericTypeInformation and allow to
            // disableGenericTypes for users
            TypeSerializer<?>[] fieldSerializers =
                    new TypeSerializer<?>[] {
                            new KryoSerializer<>(KafkaTopicPartition.class, executionConfig),
                            LongSerializer.INSTANCE,
                            LongSerializer.INSTANCE
                    };
            @SuppressWarnings("unchecked")
            Class<Tuple3<KafkaTopicPartition, Long, Long>> tupleClass =
                    (Class<Tuple3<KafkaTopicPartition, Long, Long>>) (Class<?>) Tuple3.class;
            return new TupleSerializer<>(tupleClass, fieldSerializers);
        }
    }

}
