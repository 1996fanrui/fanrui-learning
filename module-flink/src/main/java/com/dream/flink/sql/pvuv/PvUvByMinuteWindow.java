package com.dream.flink.sql.pvuv;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/9/20 15:37
 */
public class PvUvByMinuteWindow {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        CheckpointUtil.setConfYamlStateBackend(env);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
            .filter(Objects::nonNull);

        // 定义了 proc_ts，process Time 列
        tableEnv.createTemporaryView("order_table", orderStream,
            "proc_ts.proctime, orderId, userId, goodsId, price, cityId"); // ts.rowtime

        // 分钟级窗口定义
        String querySql = "select count(*)\n" +
            "      ,count(distinct userId)\n" +
            "      ,FROM_UNIXTIME(CAST(TUMBLE_END(proc_ts, INTERVAL '1' MINUTE) AS BIGINT), 'yyyy-MM-dd HH:mm')" +
            "  from order_table\n" +
            "group by TUMBLE(proc_ts, INTERVAL '1' MINUTE)";

        // userId 打散解决数据倾斜
//        querySql = "select FROM_UNIXTIME(window_end, 'yyyy-MM-dd HH:mm')\n" +
//            "      ,sum(part_pv)\n" +
//            "      ,sum(part_uv) \n" +
//            "  from \n" +
//            "    (\n" +
//            "        select count(*) as part_pv\n" +
//            "              ,count(distinct userId) as part_uv\n" +
//            "              ,CAST(TUMBLE_END(proc_ts, INTERVAL '1' MINUTE) AS BIGINT) AS window_end \n" +
//            "          from order_table\n" +
//            "        group by TUMBLE(proc_ts, INTERVAL '1' MINUTE)\n" +
//            "                ,mod(cast(userId as int), 1024)\n" +
//            "    )\n" +
//            "group by window_end";

        Table query = tableEnv.sqlQuery(querySql);

        tableEnv.toRetractStream(query, Row.class).print();

        env.execute(PvUvOfAccumulate.class.getSimpleName());
    }

}
