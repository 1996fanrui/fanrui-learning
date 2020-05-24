package com.dream.flink.func.source;

import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.concurrent.TimeUnit;

public class DataGenerator extends RichParallelSourceFunction<String>
        implements CheckpointedFunction {

    private int subtaskIndex = -1;
    ListState<String> listState;

    @Override
    public void run(SourceContext sourceContext) throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(10);
            sourceContext.collect("data");
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        listState.clear();
        listState.add("<" + subtaskIndex + "," + context.getCheckpointId() + ">");
        System.out.println("snapshotState  subtask: " + subtaskIndex + " --  CheckPointId: " + context.getCheckpointId());
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        subtaskIndex = getRuntimeContext().getIndexOfThisSubtask();

        // 按照 UnionListState 恢复
//        listState = context.getOperatorStateStore().getUnionListState(
        // 按照 ListState 恢复
        listState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<>("listState", String.class));
        if (context.isRestored()) {
            for (String indexOfSubtaskState : listState.get()) {
                System.out.println("restore ListState  currentSubtask: " + subtaskIndex +
                        " restoreSubtask and  restoreCheckPointId :" + indexOfSubtaskState);
            }
        }

        System.out.println("subtask: " + subtaskIndex + "  complete restore");
    }
}
