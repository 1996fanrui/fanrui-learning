package com.dream.flink.sql.pvuv;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/9/20 14:19
 */
public class PvUvOfAccumulate {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getBlinkTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
            .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream,
            "time, orderId, userId, goodsId, price, cityId");

        String querySql = "select count(*)," +
            "       count(distinct userId)" +
            "  from order_table";

        // 优化后的 sql，解决了数据倾斜，将全量数据根据 userId 打散成 1024 个桶，
        // 分桶内去重，最后聚合
//        querySql = "select sum(part_pv)\n" +
//            "      ,sum(part_uv)\n" +
//            "  from \n" +
//            "    (\n" +
//            "        select count(*) as part_pv\n" +
//            "              ,count(distinct userId) as part_uv\n" +
//            "          from order_table \n" +
//            "        group by mod(cast(userId as int), 1024)\n" +
//            "    )";

        Table query = tableEnv.sqlQuery(querySql);

        tableEnv.toRetractStream(query, Row.class).print();

//        System.out.println(env.getExecutionPlan());
        env.execute(PvUvOfAccumulate.class.getSimpleName());
    }

}
