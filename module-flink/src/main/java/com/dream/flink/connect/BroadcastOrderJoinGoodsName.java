package com.dream.flink.connect;

import com.alibaba.fastjson.JSON;
import com.dream.flink.connect.model.Goods;
import com.dream.flink.connect.model.Order;
import com.dream.flink.util.CheckpointUtil;
import com.dream.flink.util.KafkaConfigUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.util.Collector;

import java.util.Objects;

/**
 * @author fanrui
 * @time 2020-03-29 14:58:50
 * 使用 broadcast 实时更新商品Id 与 商品名称的映射关系。
 * 实时处理 订单信息时，需要将订单信息与对应的商品名称进行拼接，一起发送到下游。
 */
public class BroadcastOrderJoinGoodsName {

    private static final String KAFKA_CONSUMER_GROUP_ID = "console-consumer-93645";

    private static final String JOB_NAME = KAFKA_CONSUMER_GROUP_ID;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        // 测试使用，线上千万不能配置 Memory
        CheckpointUtil.setMemoryStateBackend(env);

        // 读取订单数据，读取的是 json 类型的字符串
        FlinkKafkaConsumerBase<String> consumerBigOrder =
                new FlinkKafkaConsumer011<>("order_topic_name",
                        new SimpleStringSchema(),
                        KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
                        .setStartFromGroupOffsets();

        // 读取订单数据，从 json 解析成 Order 类，
        SingleOutputStreamOperator<Order> orderStream = env.addSource(consumerBigOrder)
                // 有状态算子一定要配置 uid
                .uid("order_topic_name")
                // 过滤掉 null 数据
                .filter(Objects::nonNull)
                // 将 json 解析为 Order 类
                .map(str -> JSON.parseObject(str, Order.class));

        // 读取商品 id 与 商品名称的映射关系维表信息
        FlinkKafkaConsumerBase<String> consumerSmallOrder =
                new FlinkKafkaConsumer011<>("goods_dim_topic_name",
                        new SimpleStringSchema(),
                        KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
                        .setStartFromGroupOffsets();

        // 读取商品 ID 和 名称的映射信息，从 json 解析成 Goods 类
        SingleOutputStreamOperator<Goods> goodsDimStream = env.addSource(consumerSmallOrder)
                .uid("goods_dim_topic_name")
                .filter(Objects::nonNull)
                .map(str -> JSON.parseObject(str, Goods.class));

        // 存储 维度信息的 MapState
        final MapStateDescriptor<Integer, String> GOODS_STATE = new MapStateDescriptor<>(
                "GOODS_STATE",
                BasicTypeInfo.INT_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO);

        SingleOutputStreamOperator<Tuple2<Order, String>> resStream = orderStream
                // 订单流与 维度信息的广播流进行 connect
                .connect(goodsDimStream.broadcast(GOODS_STATE))
                .process(new BroadcastProcessFunction<Order, Goods, Tuple2<Order, String>>() {

                    // 处理 订单信息，将订单信息与对应的商品名称进行拼接，一起发送到下游。
                    @Override
                    public void processElement(Order order,
                                               ReadOnlyContext ctx,
                                               Collector<Tuple2<Order, String>> out)
                            throws Exception {
                        ReadOnlyBroadcastState<Integer, String> broadcastState =
                                ctx.getBroadcastState(GOODS_STATE);
                        // 从状态中获取 商品名称，拼接后发送到下游
                        String goodsName = broadcastState.get(order.getGoodsId());
                        out.collect(Tuple2.of(order, goodsName));
                    }

                    // 更新商品的维表信息到状态中
                    @Override
                    public void processBroadcastElement(Goods goods,
                                                        Context ctx,
                                                        Collector<Tuple2<Order, String>> out)
                            throws Exception {
                        BroadcastState<Integer, String> broadcastState =
                                ctx.getBroadcastState(GOODS_STATE);
                        if (goods.isRemove()) {
                            // 商品下架了，应该要从状态中移除，否则状态将无限增大
                            broadcastState.remove(goods.getGoodsId());
                        } else {
                            // 商品上架，应该添加到状态中，用于关联商品信息
                            broadcastState.put(goods.getGoodsId(), goods.getGoodsName());
                        }
                    }
                });

        // 结果进行打印，生产环境应该是输出到外部存储
        resStream.print();

        env.execute(JOB_NAME);
    }
}
