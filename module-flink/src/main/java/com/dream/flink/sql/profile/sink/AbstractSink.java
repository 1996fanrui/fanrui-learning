package com.dream.flink.sql.profile.sink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.types.Row;

/**
 *
 * @author fanrui
 *
 */
public abstract class AbstractSink extends RichSinkFunction<Row> implements CheckpointedFunction {

    protected boolean needProcess;

    AbstractSink() {
        System.out.println("AbstractSink 构造器");
        this.needProcess = false;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        System.out.println("MySink open");
        int indexOfThisSubtask = getRuntimeContext().getIndexOfThisSubtask();
        if (indexOfThisSubtask == 1) {
            needProcess = true;
        } else {
            needProcess = false;
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        System.out.println("snapshotState checkpoint id:" + context.getCheckpointId());
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        System.out.println("initializeState .");
    }

    @Override
    public void invoke(Row value, Context context) throws Exception {
        if (needProcess) {
            doProcess(value, context);
        }
        System.out.println(value);
    }

    protected abstract void doProcess(Row value, Context context) throws Exception;

}
