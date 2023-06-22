package com.dream.flink.sql.pvuv;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/9/20 14:19
 */
public class GroupByPvUvOfMinuteWindow {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
            .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream);

        String querySql = "select cityId" +
            "      ,FROM_UNIXTIME(CAST(TUMBLE_END(proc_ts, INTERVAL '1' MINUTE) AS BIGINT), 'yyyy-MM-dd HH:mm')\n" +
            "      ,count(*) as pv\n" +
            "      ,count(distinct userId) as uv\n" +
            "  from order_table \n" +
            "group by TUMBLE(proc_ts, INTERVAL '1' MINUTE)\n" +
            "        ,cityId";

        // userId 打散解决数据倾斜
//        querySql = "select cityId\n" +
//            "      ,FROM_UNIXTIME(window_end, 'yyyy-MM-dd HH:mm')\n" +
//            "      ,sum(part_pv)\n" +
//            "      ,sum(part_uv) \n" +
//            "  from \n" +
//            "    (\n" +
//            "        select cityId\n" +
//            "              ,CAST(TUMBLE_END(proc_ts, INTERVAL '1' MINUTE) AS BIGINT) AS window_end\n" +
//            "              ,count(*) as part_pv\n" +
//            "              ,count(distinct userId) as part_uv\n" +
//            "          from order_table \n" +
//            "        group by TUMBLE(proc_ts, INTERVAL '1' MINUTE)\n" +
//            "                ,cityId\n" +
//            "                ,mod(cast(userId as int), 1024)\n" +
//            "    )\n" +
//            "group by cityId\n" +
//            "        ,window_end";

        Table query = tableEnv.sqlQuery(querySql);

        tableEnv.toRetractStream(query, Row.class).print();

//        System.out.println(env.getExecutionPlan());
        env.execute(GroupByPvUvOfMinuteWindow.class.getSimpleName());
    }

}
