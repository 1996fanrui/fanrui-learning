package com.dream.flink.sql.multisink;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/12/2 22:02
 * 验证单个 Job 中的单个表是否支持写多个 Sink
 *
 * Flink 1.11 官网 : https://ci.apache.org/projects/flink/flink-docs-release-1.11/zh/dev/table/sql/insert.html
 * 输出样例数据如下所示，可以输出到多个 sink：
 * +I(sink0,1606923785820,orderId:1634)
 * +I(sink1,1606923785820)
 * +I(sink0,1606923785821,orderId:1635)
 * +I(sink1,1606923785821)
 * +I(sink0,1606923785822,orderId:1636)
 * +I(sink1,1606923785822)
 * +I(sink0,1606923785823,orderId:1637)
 * +I(sink1,1606923785823)
 * +I(sink0,1606923785825,orderId:1638)
 * +I(sink1,1606923785825)
 */
public class MultiSinkDemo {

    private static final String PRINT0_SINK_DDL = "create table print0_sink ( \n" +
            " flag STRING, " +
            " ts BIGINT, " +
            " orderId STRING" +
            ") with ('connector' = 'print' )";

    private static final String PRINT1_SINK_DDL = "create table print1_sink ( \n" +
            " flag STRING, " +
            " ts BIGINT" +
            ") with ('connector' = 'print' )";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        CheckpointUtil.setConfYamlStateBackend(env);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
                .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream,
                "ts, orderId, userId, goodsId, price, cityId");


        tableEnv.executeSql(PRINT0_SINK_DDL);
        tableEnv.executeSql(PRINT1_SINK_DDL);

        StatementSet stmtSet = tableEnv.createStatementSet();
        stmtSet.addInsertSql("insert into print0_sink select 'sink0', ts, orderId from order_table");
        stmtSet.addInsertSql("insert into print1_sink select 'sink1', ts from order_table");

        stmtSet.execute();
    }

}
