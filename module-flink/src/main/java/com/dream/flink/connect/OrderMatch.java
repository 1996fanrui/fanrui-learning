package com.dream.flink.connect;

import com.alibaba.fastjson.JSON;
import com.dream.flink.connect.model.Order;
import com.dream.flink.util.CheckpointUtil;
import com.dream.flink.util.KafkaConfigUtil;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Objects;

/**
 * @author fanrui
 * @time 2020-03-29 09:22:39
 * 场景：一个订单分成了 大订单和小订单，需要在数据流中按照订单 Id 进行匹配，
 * 默认认为数据流的延迟最大为 60s。
 * 大订单和小订单匹配成功后向下游发送，若 60s 还未匹配成功，则测流输出
 * <p>
 * 思路：
 * 提取时间戳，按照 orderId 进行 keyBy，然后两个 流 connect，
 * 大订单和小订单的处理逻辑一样，两个流通过 State 进行关联。
 * 来了一个流，需要保存到自己的状态中，并注册一个 60s 之后的定时器。
 * 如果 60s 内来了第二个流，则将两个数据拼接发送到下游。
 * 如果 60s 内第二个流还没来，就会触发 onTimer，然后进行侧流输出。
 */
public class OrderMatch {

    private static final String KAFKA_CONSUMER_GROUP_ID = "console-consumer-93645";

    private static final String KAFKA_TOPIC = "order-log";

    private static final String JOB_NAME = KAFKA_CONSUMER_GROUP_ID;


    private static OutputTag<Order> bigOrderTag = new OutputTag<>("bigOrder");
    private static OutputTag<Order> smallOrderTag = new OutputTag<>("smallOrder");


    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        // 测试使用，线上千万不能配置 Memory
        CheckpointUtil.setMemoryStateBackend(env);

        // 读取大订单数据，读取的是 json 类型的字符串
        FlinkKafkaConsumerBase<String> consumerBigOrder =
                new FlinkKafkaConsumer011<>("big_order_topic_name",
                        new SimpleStringSchema(),
                        KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
                        .setStartFromGroupOffsets();

        // 读取大订单数据，从 json 解析成 Order 类，
        // 提取 EventTime，分配 WaterMark。按照 订单id 进行 keyBy
        KeyedStream<Order, String> bigOrderStream = env.addSource(consumerBigOrder)
                // 有状态算子一定要配置 uid
                .uid("big_order_topic_name")
                // 过滤掉 null 数据
                .filter(Objects::nonNull)
                // 将 json 解析为 Order 类
                .map(str -> JSON.parseObject(str, Order.class))
                // 提取 EventTime，分配 WaterMark
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<Order>
                                (Time.seconds(60)) {
                            @Override
                            public long extractTimestamp(Order order) {
                                return order.getTime();
                            }
                        })
                // 按照 订单id 进行 keyBy
                .keyBy(Order::getOrderId);

        // 小订单处理逻辑与大订单完全一样
        FlinkKafkaConsumerBase<String> consumerSmallOrder =
                new FlinkKafkaConsumer011<>("small_order_topic_name",
                        new SimpleStringSchema(),
                        KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
                        .setStartFromGroupOffsets();

        KeyedStream<Order, String> smallOrderStream = env.addSource(consumerSmallOrder)
                .uid("small_order_topic_name")
                .filter(Objects::nonNull)
                .map(str -> JSON.parseObject(str, Order.class))
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<Order>
                                (Time.seconds(10)) {
                            @Override
                            public long extractTimestamp(Order order) {
                                return order.getTime();
                            }
                        })
                .keyBy(Order::getOrderId);

        // 使用 connect 连接大小订单的流，然后使用 CoProcessFunction 进行数据匹配
        SingleOutputStreamOperator<Tuple2<Order, Order>> resStream = bigOrderStream
                .connect(smallOrderStream)
                .process(new CoProcessFunction<Order, Order, Tuple2<Order, Order>>() {

                    // 大订单数据先来了，将大订单数据保存在 bigState 中。
                    ValueState<Order> bigState;
                    // 小订单数据先来了，将小订单数据保存在 smallState 中。
                    ValueState<Order> smallState;
                    // 当前注册的 定时器的 时间戳
                    ValueState<Long> timerState;

                    // 大订单的处理逻辑
                    @Override
                    public void processElement1(Order bigOrder, Context ctx,
                                                Collector<Tuple2<Order, Order>> out)
                            throws Exception {
                        // 获取当前 小订单的状态值
                        Order smallOrder = smallState.value();
                        // smallOrder 不为空表示小订单先来了，直接将大小订单拼接发送到下游
                        if (smallOrder != null) {
                            out.collect(Tuple2.of(smallOrder, bigOrder));
                            // 清空小订单对应的 State 信息
                            smallState.clear();
                            // 这里可以将 Timer 清除。因为两个流都到了，没必要再触发 onTimer 了
                            ctx.timerService().deleteEventTimeTimer(timerState.value());
                            timerState.clear();
                        } else {
                            // 小订单还没来，将大订单放到状态中，并注册 1 分钟之后触发的 timerState
                            bigState.update(bigOrder);
                            // 1 分钟后触发定时器，并将定时器的触发时间保存在 timerState 中
                            long time = bigOrder.getTime() + 60000;
                            timerState.update(time);
                            ctx.timerService().registerEventTimeTimer(time);
                        }
                    }

                    @Override
                    public void processElement2(Order smallOrder, Context ctx,
                                                Collector<Tuple2<Order, Order>> out)
                            throws Exception {
                        // 小订单的处理逻辑与大订单的处理逻辑完全类似
                        Order bigOrder = bigState.value();
                        if (bigOrder != null) {
                            out.collect(Tuple2.of(smallOrder, bigOrder));
                            ctx.timerService().deleteEventTimeTimer(timerState.value());
                            bigState.clear();
                            timerState.clear();
                        } else {
                            smallState.update(smallOrder);
                            long time = smallOrder.getTime() + 60000;
                            timerState.update(time);
                            ctx.timerService().registerEventTimeTimer(time);
                        }
                    }

                    @Override
                    public void onTimer(long timestamp, OnTimerContext ctx,
                                        Collector<Tuple2<Order, Order>> out)
                            throws Exception {
                        // 定时器触发了，即 1 分钟内没有接收到两个流。
                        // 大订单不为空，则将大订单信息侧流输出
                        if (bigState.value() != null) {
                            ctx.output(bigOrderTag, bigState.value());
                        }
                        // 小订单不为空，则将小订单信息侧流输出
                        if (smallState.value() != null) {
                            ctx.output(smallOrderTag, smallState.value());
                        }
                        // 清空状态信息
                        bigState.clear();
                        smallState.clear();
                        timerState.clear();
                    }

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        // 初始化状态信息
                        bigState = getRuntimeContext().getState(
                                new ValueStateDescriptor<>("bigState", Order.class));
                        smallState = getRuntimeContext().getState(
                                new ValueStateDescriptor<>("smallState", Order.class));
                        timerState = getRuntimeContext().getState(
                                new ValueStateDescriptor<>("timerState", Long.class));
                    }
                });

        // 正常匹配到数据的 输出。生产环境肯定是需要通过 Sink 输出到外部系统
        resStream.print();

        // 只有大订单时，没有匹配到 小订单，属于异常数据，需要保存到外部系统，进行特殊处理
        resStream.getSideOutput(bigOrderTag).print();
        // 只有小订单时，没有匹配到 大订单，属于异常数据，需要保存到外部系统，进行特殊处理
        resStream.getSideOutput(smallOrderTag).print();

        env.execute(JOB_NAME);
    }


}
