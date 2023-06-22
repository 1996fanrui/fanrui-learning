package com.dream.flink.sql.pvuv;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @author fanrui
 * @date 2021-11-17 14:57:14
 */
public class GroupByPvUvOfCumulateWindow {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(2);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
            .filter(Objects::nonNull);

        tableEnv.createTemporaryView("order_table", orderStream, $("orderId"), $("userId"),
                $("goodsId"), $("price"), $("cityId"), $("proc_ts").proctime());

        String querySql = "SELECT window_start\n" +
                "       ,window_end\n" +
                "       ,cityId\n" +
                "       ,count(*) as pv\n" +
                "       ,count(distinct userId) as uv\n" +
                "FROM TABLE(\n" +
                "    CUMULATE(TABLE order_table, DESCRIPTOR(proc_ts), INTERVAL '1' MINUTES, INTERVAL '24' HOURS))\n" +
                "GROUP BY window_start\n" +
                "       ,window_end\n" +
                "       ,cityId";

        Table query = tableEnv.sqlQuery(querySql);

        tableEnv.toRetractStream(query, Row.class).print();

        env.execute(GroupByPvUvOfCumulateWindow.class.getSimpleName());
    }

}
