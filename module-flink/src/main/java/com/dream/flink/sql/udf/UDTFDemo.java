package com.dream.flink.sql.udf;

import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.annotation.DataTypeHint;
import org.apache.flink.table.annotation.FunctionHint;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.types.Row;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class UDTFDemo {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.enableCheckpointing(TimeUnit.SECONDS.toMillis(10));

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        String sourceDDL = "CREATE TABLE orders (\n" +
                "  app          INT,\n" +
                "  channel      INT,\n" +
                "  user_id      STRING,\n" +
                "  ts           TIMESTAMP(3),\n" +
                "  WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                "   'connector' = 'datagen',\n" +
                "   'rows-per-second'='1000',\n" +
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

//        tableEnv.createTemporaryFunction("test_udtf", TestTableFunction.class);
        tableEnv.createTemporaryFunction("test_udtf", TestRowTableFunction.class);

        tableEnv.executeSql("insert into sink_table " +
                "select app" +
                "       ,channel" +
                "       ,user_id" +
                "       ,user_id_new" +
                "       ,new_id" +
                "       ,ts" +
                " from orders " +
                // 返回值是单列的 udtf
//                "JOIN LATERAL TABLE(test_udtf(user_id)) as t(user_id_new)\n" +
                // 返回值是多列的 udtf
                "JOIN LATERAL TABLE(test_udtf(user_id)) as t(user_id_new, new_id)\n" +
                "on true");
    }

    // 返回值是单列的 udtf
    public static class TestTableFunction extends TableFunction<String> {
        public void eval(String s) {
            collect(heavyOperation(s) + "1");
            collect(heavyOperation(s) + "2");
        }
    }

    // 返回值是多列的 udtf
    @FunctionHint(output = @DataTypeHint("ROW<user_id_new STRING, new_id INT>"))
    public static class TestRowTableFunction extends TableFunction<Row> {
        public void eval(String s) {
            collect(Row.of(heavyOperation(s), 1));
            collect(Row.of(heavyOperation(s), 2));
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
