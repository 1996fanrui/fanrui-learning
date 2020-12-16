package com.dream.flink.window;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.func.aggregate.SumAggregate;
import com.dream.flink.util.CheckpointUtil;
import com.dream.flink.window.model.CityUserCostOfCurWindow;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author fanrui
 * @time 2020-03-29 20:49:21
 * 5 分钟滚动窗口，根据订单交易日志，
 * 计算每个窗口内，每个城市，消费最高的 100 个用户
 * 假设数据延迟最大为 10s，即：WaterMark 最大延迟设置为 10s
 */
public class WindowTopN {

    private static final String KAFKA_CONSUMER_GROUP_ID = "console-consumer-93645";

    private static final String JOB_NAME = KAFKA_CONSUMER_GROUP_ID;

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        // 测试使用，线上千万不能配置 Memory
        CheckpointUtil.setConfYamlStateBackend(env);

        // 读取订单数据，读取的是 json 类型的字符串
//        FlinkKafkaConsumerBase<String> consumerBigOrder =
//                new FlinkKafkaConsumer011<>("order_topic_name",
//                        new SimpleStringSchema(),
//                        KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
//                        .setStartFromGroupOffsets();

        // 读取订单数据，从 json 解析成 Order 类，
        SingleOutputStreamOperator<Order> orderStream = env.addSource(new OrderGenerator())
            // 有状态算子一定要配置 uid
            .uid("order_topic_name")
            // 过滤掉 null 数据
            .filter(Objects::nonNull)
            // 将 json 解析为 Order 类
//                .map(str -> JSON.parseObject(str, Order.class))
            .assignTimestampsAndWatermarks(
                new BoundedOutOfOrdernessTimestampExtractor<Order>(Time.seconds(10)) {
                    @Override
                    public long extractTimestamp(Order order) {
                        return order.getTs();
                    }
                });

        SingleOutputStreamOperator<CityUserCostOfCurWindow> res = orderStream
            .keyBy(new KeySelector<Order, Tuple2<Integer, String>>() {
                @Override
                public Tuple2<Integer, String> getKey(Order order) throws Exception {
                    return Tuple2.of(order.getCityId(), order.getUserId());
                }
            })
            .timeWindow(Time.minutes(5))
            .aggregate(new SumAggregate<>(Order::getPrice), new CityUserCostWindowFunction())
            .keyBy(new KeySelector<CityUserCostOfCurWindow, Tuple2<Long, Integer>>() {
                @Override
                public Tuple2<Long, Integer> getKey(CityUserCostOfCurWindow entry) throws Exception {
                    return Tuple2.of(entry.getTime(), entry.getCityId());
                }
            })
            .process(new CityUserCostTopN());

        res.print();

        env.execute(JOB_NAME);
    }


    // 将 按照 city 和 user 当前窗口对应 cost 组合好发送到下游
    private static class CityUserCostWindowFunction implements
        WindowFunction<Long, CityUserCostOfCurWindow,
            Tuple2<Integer, String>, TimeWindow> {
        @Override
        public void apply(Tuple2<Integer, String> key,
                          TimeWindow window,
                          Iterable<Long> input,
                          Collector<CityUserCostOfCurWindow> out)
            throws Exception {
            long cost = input.iterator().next();
            out.collect(new CityUserCostOfCurWindow(window.getEnd(), key.f0, key.f1, cost));
        }
    }

    // 计算当前窗口，当前城市，消耗最多的 N 个用户
    private static class CityUserCostTopN extends
        KeyedProcessFunction<Tuple2<Long, Integer>,
            CityUserCostOfCurWindow, CityUserCostOfCurWindow> {
        // 默认求 Top 100
        int n = 100;

        public CityUserCostTopN(int n) {
            this.n = n;
        }

        public CityUserCostTopN() {
        }

        // 存储当前窗口的所有用户的数据，待收齐同一个窗口的数据后，再触发 Top N 计算
        private ListState<CityUserCostOfCurWindow> entryState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            ListStateDescriptor<CityUserCostOfCurWindow> itemViewStateDesc = new ListStateDescriptor<>(
                "entryState", CityUserCostOfCurWindow.class);
            entryState = getRuntimeContext().getListState(itemViewStateDesc);
        }

        @Override
        public void processElement(CityUserCostOfCurWindow entry,
                                   Context ctx,
                                   Collector<CityUserCostOfCurWindow> out) throws Exception {
            // 在这里根本不知道当前窗口的数据是否全到了，所以只好利用 onTimer 来做
            entryState.add(entry);
            //注册 windowEnd+1 的 EventTime Timer, 当触发时，说明收集好了所有 windowEnd的商品数据
            ctx.timerService().registerEventTimeTimer(entry.getTime() + 1);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<CityUserCostOfCurWindow> out) throws Exception {
            ArrayList<CityUserCostOfCurWindow> allEntry = new ArrayList<>();
            for (CityUserCostOfCurWindow item : entryState.get()) {
                allEntry.add(item);
            }
//            sortTopN(allEntry, new CostComparator(), n);
            for (int i = 0; i < n; i++) {
                out.collect(allEntry.get(i));
            }
            entryState.clear();
        }


        // 将前 N 个最大的元素，放到
        private static <T> void sortTopN(List<T> list, Comparator<? super T> c, int N) {
            T[] array = (T[]) list.toArray();
            int L = 0;
            int R = list.size() - 1;
            while (L != R) {
                int partition = recSortTopN(array, L, R, c);
                // 第 N 个位置已经拍好序
                if (partition == N) {
                    return;
                } else if (partition < N) {
                    L = partition + 1;
                } else {
                    R = partition - 1;
                }
            }
        }

        private static <E> int recSortTopN(E[] array, int L, int R,
                                           Comparator<? super E> c) {
            // 将 L mid R 三个位置的中位数的 index 返回，并将其交换到 L 的位置
            int mid = getMedian(array, L, R, c);
            if (mid != L) {
                swap(array, L, mid);
            }

            // 小的放左边，大的放右边
            E pivot = array[L];
            int i = L;
            int j = R;
            while (i < j) {
                // i 位置小于 等于 pivot，则 i 一直右移
                while (c.compare(array[i], pivot) <= 0 && i < j) {
                    i++;
                }
                // j 位置大于 等于 pivot，则 j 一直左移
                while (c.compare(array[i], pivot) <= 0 && i < j) {
                    j--;
                }
                if (i < j) {
                    swap(array, i, j);
                }
            }
            swap(array, L, i);
            return i;
        }


        private static <E> void swap(E[] array, int i, int j) {
            E tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }


        private static <E> int getMedian(E[] array, int L, int R,
                                         Comparator<? super E> c) {
            int mid = L + ((R - L) >> 1);
            // 拿到三个元素的值，返回中间元素 的 index
            E valueL = array[L];
            E valueMid = array[mid];
            E valueR = array[R];
            if (c == null) {
                return 0;
            }
            if (c.compare(valueL, valueMid) <= 0
                && c.compare(valueMid, valueR) <= 0) {
                return mid;
            } else if (c.compare(valueMid, valueL) <= 0
                && c.compare(valueL, valueR) <= 0) {
                return L;
            } else {
                return R;
            }
        }
    }

    private static class CostComparator implements Comparator<CityUserCostOfCurWindow> {
        @Override
        public int compare(CityUserCostOfCurWindow o1, CityUserCostOfCurWindow o2) {
            long diff = o2.getCost() - o1.getCost();
            return diff == 0 ? 0 : diff > 0 ? 1 : -1;
        }
    }
}
