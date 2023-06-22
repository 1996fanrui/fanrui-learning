package com.dream.flink.sql.udf;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.connector.source.AsyncTableFunctionProvider;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.LookupTableSource;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.functions.AsyncTableFunction;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.types.Row;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

public class LookupJoinDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(10));

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  proctime as  PROCTIME(),\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='5',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";
        tableEnv.executeSql(sourceDDL);

        String sinkDDL = "CREATE TABLE sink_table (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  user_id_new  STRING,\n" +
                "  new_id       INT,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'print'\n" +
                ")";
        tableEnv.executeSql(sinkDDL);

        tableEnv.executeSql("create table test_async_table (user_id string, user_id_new string, new_id INT)" +
                " with ('connector'='async-table-func')");

        tableEnv.executeSql("insert into sink_table " +
                "select app" +
                "       ,channel" +
                "       ,orders.user_id" +
                "       ,user_id_new" +
                "       ,new_id" +
                "       ,ts" +
                " from orders " +
                "join test_async_table for system_time as of proctime " +
                "on orders.user_id = test_async_table.user_id");
    }

    public static class AsyncTableFunctionSourceFactory implements DynamicTableSourceFactory {

        @Override
        public DynamicTableSource createDynamicTableSource(Context context) {
            return new AsyncTableFunctionSource();
        }

        @Override
        public String factoryIdentifier() {
            return "async-table-func";
        }

        @Override
        public Set<ConfigOption<?>> requiredOptions() {
            return Collections.emptySet();
        }

        @Override
        public Set<ConfigOption<?>> optionalOptions() {
            return Collections.emptySet();
        }
    }

    public static class AsyncTableFunctionSource implements LookupTableSource {

        public AsyncTableFunctionSource() {
        }

        @Override
        public DynamicTableSource copy() {
            return new AsyncTableFunctionSource();
        }

        @Override
        public String asSummaryString() {
            return "AsyncTableFunctionSource";
        }

        @Override
        public LookupRuntimeProvider getLookupRuntimeProvider(LookupContext lookupContext) {
            return AsyncTableFunctionProvider.of(new TestAsyncTableFunction());
        }
    }


    @FunctionHint(output = @DataTypeHint("ROW<user_id STRING, user_id_new STRING, new_id INT>"))
    public static class TestAsyncTableFunction extends AsyncTableFunction<Row> {
        ExecutorService executor;

        @Override
        public void open(FunctionContext context) {
            this.executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());
        }

        /**
         * AsyncTableFunction 的理解与使用:
         * 1. Async 属于顺序处理，依次等待输入元素结果。即：前一条数据不 complete，后面的数据就算 complete ，也不会发送给下游
         * 2. 输出结果通过 resultFuture.complete 返回，且每个 input 只能调用一次 complete，且必须调用一次。
         *      就算没有返回值，也必须调用 complete，否则后续的数据一直在等这条数据的结果。
         *      如果没有返回值，可以返回 resultFuture.complete(Collections.emptyList());
         * 3.
         *
         */
        public void eval(CompletableFuture<Collection<Row>> resultFuture, String userId) {
            CompletableFuture.supplyAsync(() -> Row.of(userId, heavyOperation(userId), 1), executor)
                    .thenAccept(result -> resultFuture.complete(Collections.singleton(result)));
        }

        @Override
        public void close() throws Exception {
            executor.shutdown();
        }
    }

    private static String heavyOperation(String input) {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        return input + "   a   ";
    }

}
