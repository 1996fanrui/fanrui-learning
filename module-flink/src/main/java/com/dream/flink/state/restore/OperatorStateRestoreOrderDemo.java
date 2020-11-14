package com.dream.flink.state.restore;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fanrui03
 * @time 2020-05-24 14:49:14
 * 结论：
 * 1、 如果 State 中的 Pojo 类名变了，状态不能正常恢复
 * 2、 如果 State 中的 Pojo 增加了字段，状态不能恢复（字段变了，或减字段，没有做测试）
 */
public class OperatorStateRestoreOrderDemo {

    private static final String JOB_NAME = "hdp-operator-order-state-demo";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(2);
        CheckpointUtil.setConfYamlStateBackend(env);

        env.addSource(new OrderGenerator())
                .filter(Objects::nonNull)
                .map(new OperatorStateMapFunction())
                .print();

        env.execute(JOB_NAME);
    }

    public static class OperatorStateMapFunction
            extends RichMapFunction<Order, String>
            implements CheckpointedFunction {

        private int subtaskIndex = -1;
        ListState<Order> listState;
        private final List<Order> buffer = new ArrayList<>();

        @Override
        public String map(Order order) throws Exception {
            buffer.add(order);
            return order.getOrderId();
        }

        @Override
        public void snapshotState(
                FunctionSnapshotContext context) throws Exception {
            listState.clear();
            listState.addAll(buffer);
            buffer.clear();
            System.out.println("snapshotState  subtask: " + subtaskIndex +
                    " --  CheckPointId: " + context.getCheckpointId());
        }

        @Override
        public void initializeState(
                FunctionInitializationContext context) throws Exception {

            subtaskIndex = getRuntimeContext().getIndexOfThisSubtask();
            // 按照 ListState 恢复
            listState = context.getOperatorStateStore().getListState(
                    new ListStateDescriptor<>("listState", Order.class));
            if (context.isRestored()) {
                for (Order order : listState.get()) {
                    System.out.println("restore ListState  currentSubtask: " + subtaskIndex +
                            " restoreOrderId :" + order.getOrderId());
                }
            }
            System.out.println("subtask: " + subtaskIndex + "  complete restore");
        }
    }

}
