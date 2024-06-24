package com.dream.flink.leak;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestOffHeapMemory {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int offHeapSize = parameterTool.getInt("offHeapSize", 1024);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='10000000000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        tableEnv.toDataStream(query, Row.class)
                .map(new RequestOffHeapMemoryMapper<>(offHeapSize))
                .addSink(new DiscardingSink<>())
                .name("MySink");

        env.execute(RequestOffHeapMemory.class.getSimpleName());
    }

    public static class RequestOffHeapMemoryMapper<T> extends RichMapFunction<T, T> {

        private final int offHeapSize;

        private volatile boolean isRunning = true;
        private volatile Object lock;

        private ExecutorService executorService;

        public RequestOffHeapMemoryMapper(int offHeapSize) {
            this.offHeapSize = offHeapSize;
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            this.lock = new Object();
            this.executorService = Executors.newSingleThreadExecutor();
            this.executorService.execute(() -> {
                while (isRunning) {
                    requestAndFreeOffHeapMemory();
                }
            });
        }

        @Override
        public T map(T value) throws Exception {
            requestAndFreeOffHeapMemory();
            return value;
        }

        private void requestAndFreeOffHeapMemory() {
            MemorySegment memorySegment;
            memorySegment = MemorySegmentFactory.allocateOffHeapUnsafeMemory(offHeapSize, lock, () -> {
            });
            memorySegment.free();
        }

        @Override
        public void close() throws Exception {
            isRunning = false;

            Thread.sleep(200);
            executorService.shutdownNow();
        }
    }

}
