package com.dream.flink.sql.pvuv;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.sql.FlinkSqlUtil;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.util.Objects;

/**
 * @author fanrui03
 * @date 2020/9/22 20:37
 * Time 相关属性参考：
 * https://ci.apache.org/projects/flink/flink-docs-release-1.10/dev/table/streaming/time_attributes.html
 */
public class PvUvByEventTimeSecondWindow {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        StreamTableEnvironment tableEnv = FlinkSqlUtil.getTableEnv(env);

        DataStream<Order> orderStream = env.addSource(new OrderGenerator())
                .filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<Order>(Time.seconds(10)) {
                            @Override
                            public long extractTimestamp(Order order) {
                                return order.ts;
                            }
                        }
                );

        // 定义了 event_ts，即：event Time 列，列名不需要与 Order 字段名一致，
        // 只需要加 .rowtime 即可标识字段为 event Time 列
        tableEnv.createTemporaryView("order_table", orderStream,
                "event_ts.rowtime, orderId, userId, goodsId, price, cityId");

        // 分钟级窗口定义
        String querySql = "select count(*)\n" +
                "      ,count(distinct userId)\n" +
                "      ,FROM_UNIXTIME(CAST(TUMBLE_END(event_ts, INTERVAL '1' SECOND) AS BIGINT), 'yyyy-MM-dd HH:mm:ss')" +
                "  from order_table\n" +
                "group by TUMBLE(event_ts, INTERVAL '1' SECOND)";

        Table query = tableEnv.sqlQuery(querySql);

        tableEnv.toRetractStream(query, Row.class).print();

        env.execute(PvUvOfAccumulate.class.getSimpleName());
    }

}
