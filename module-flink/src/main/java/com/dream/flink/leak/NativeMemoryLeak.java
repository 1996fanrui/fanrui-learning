package com.dream.flink.leak;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NativeMemoryLeak {

    private static final Logger LOG = LoggerFactory.getLogger(NativeMemoryLeak.class);

    public static void main(String[] args) throws Exception {
        // Allocate 20MB every second
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String memoryText = parameterTool.get("allocateSize", "50MB");
        final MemorySize memorySize = MemorySize.parse(memoryText);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1000000',\n" +
                "   'fields.app.min'='1',\n" +
                "   'fields.app.max'='10',\n" +
                "   'fields.channel.min'='21',\n" +
                "   'fields.channel.max'='30',\n" +
                "   'fields.user_id.length'='10'\n" +
                ")";

        tableEnv.executeSql(sourceDDL);

        Table query = tableEnv.sqlQuery("select * from orders");
        tableEnv.toAppendStream(query, Row.class)
                .rebalance()
                .addSink(new MemoryLeakSink((int) memorySize.getBytes()))
                .name("MySink");

        env.execute(NativeMemoryLeak.class.getSimpleName());
    }

    private static class MemoryLeakSink extends RichSinkFunction<Row> {

        private final int maxByteSize;
        private final Random random = new Random();

        private MemoryLeakSink(int maxByteSize) {
            this.maxByteSize = maxByteSize;
        }

        @Override
        public void invoke(Row value, Context context) throws InterruptedException {
            if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                int byteSize = random.nextInt(maxByteSize);
                byte[] bytes = new byte[byteSize];
                new Random().nextBytes(bytes);
                try {
                    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    Unsafe unsafe = (Unsafe) theUnsafe.get(null);
                    long address = unsafe.allocateMemory(byteSize);
                    for (int i = 0; i < byteSize; i++) {
                        unsafe.putByte(address + i, bytes[i]);
                    }
                } catch (Exception e) {
                    LOG.info("Unsafe exception", e);
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }
}
