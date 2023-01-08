package com.dream.flink.connect;

import com.google.common.collect.Lists;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarTrafficIncidentJoin {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<CarLocationEvent> carLocationStream = env.fromElements(
                // 10 一直没动
                // 11 在动
                // 12 只有一条记录，认为没动
                // 13 在动
                new CarLocationEvent(0, 0L, 10, 100),
                new CarLocationEvent(1, 0L, 11, 100),

                new CarLocationEvent(2, 1000L, 10, 100),
                new CarLocationEvent(3, 1000L, 11, 101),

                new CarLocationEvent(4, 2000L, 12, 103),
                new CarLocationEvent(5, 2000L, 11, 102),

                new CarLocationEvent(6, 3000L, 11, 100),
                new CarLocationEvent(7, 3000L, 13, 105),

                new CarLocationEvent(8, 4000L, 10, 100),
                new CarLocationEvent(9, 4000L, 13, 104)
//        ).assignTimestampsAndWatermarks(WatermarkStrategy.<CarLocationEvent>forMonotonousTimestamps()
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<CarLocationEvent>forBoundedOutOfOrderness(Duration.ofMillis(10))
                .withTimestampAssigner((ctx) -> (element, recordTimestamp) -> element.timestamp));

        DataStream<TrafficIncidentEvent> trafficIncidentStream = env.fromElements(
                // 10 一直没动，id=0 应该是可信的
                new TrafficIncidentEvent(0, 0L, Lists.newArrayList(10)),
                // 11 动了，所以不可信
                new TrafficIncidentEvent(1, 1L, Lists.newArrayList(10, 11)),
                // 10 一直没动，12 只有一条记录，所以也认为没动。 id=2 应该是可信的
                new TrafficIncidentEvent(2, 2L, Lists.newArrayList(10, 12)),
                // 11 动了，所以不可信
                new TrafficIncidentEvent(3, 3L, Lists.newArrayList(10, 11, 12)),
                // 13 动了，所以不可信
                new TrafficIncidentEvent(4, 4L, Lists.newArrayList(13)),
                // 10 没动， 14 没上报，相当于没动，也认为可信。
                new TrafficIncidentEvent(5, 5L, Lists.newArrayList(10, 14))
        ).assignTimestampsAndWatermarks(WatermarkStrategy.<TrafficIncidentEvent>forBoundedOutOfOrderness(Duration.ofMillis(10))
                .withTimestampAssigner((ctx) -> (element, recordTimestamp) -> element.timestamp));

        KeyedStream<Tuple3<Integer, Long, Integer>, Integer> splitStreamB =
                trafficIncidentStream.flatMap(new FlatMapFunction<TrafficIncidentEvent, Tuple3<Integer, Long, Integer>>() {
                    @Override
                    public void flatMap(TrafficIncidentEvent event, Collector<Tuple3<Integer, Long, Integer>> out) {
                        for (int carId : event.carIds) {
                            out.collect(Tuple3.of(event.id, event.timestamp, carId));
                        }
                    }
                }).keyBy((KeySelector<Tuple3<Integer, Long, Integer>, Integer>) value -> value.f2);

        SingleOutputStreamOperator<Tuple2<Integer, Boolean>> trafficResultOfAllCars =
                carLocationStream.keyBy((KeySelector<CarLocationEvent, Integer>) value -> value.carId)
                .connect(splitStreamB)
                .process(new CarJoinFunction());

        /*
         * (0,true)
         * (1,true)
         * trafficIncident 1 发现 car 11 位置变了
         * (1,false)
         * (2,true)
         * (2,true)
         * (3,true)
         * (3,true)
         * trafficIncident 3 发现 car 11 位置变了
         * (3,false)
         * trafficIncident 4 发现 car 13 位置变了
         * (4,false)
         * (5,true)
         * (5,true)
         */
//        trafficResultOfAllCars.print();

        trafficResultOfAllCars.keyBy(new KeySelector<Tuple2<Integer, Boolean>, Integer>() {
            @Override
            public Integer getKey(Tuple2<Integer, Boolean> value) throws Exception {
                return value.f0;
            }
        }).process(new KeyedProcessFunction<Integer, Tuple2<Integer, Boolean>, Tuple2<Integer, Boolean>>() {

            private transient ValueState<Boolean> result;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                result = getRuntimeContext().getState(
                        new ValueStateDescriptor<>("result", Boolean.class));
            }

            @Override
            public void processElement(Tuple2<Integer, Boolean> value, Context ctx, Collector<Tuple2<Integer, Boolean>> out) throws Exception {
                Boolean oldResult = result.value();
                if (oldResult == null) {
                    result.update(value.f1);
                    out.collect(value);
                    return;
                }
                // 新的结果是可信的，则不更新最终结果
                if (value.f1) {
                    return;
                }

                // 新的结果不可信，且老结果可信，则更新结果
                if (oldResult) {
                    result.update(value.f1);
                    out.collect(value);
                }
                // 新的结果不可信，且老结果不可信，则不更新结果
                // TODO 需要设计定时器 或 其他思路，清理掉 state ，保证不会残留。
                // TODO 目前结果可能会输出两次，先输出可信，过一会输出不可信，是否需要过滤掉第一次输出？
                //  如果需要过滤，也需要定时器，类似等待的操作，等待交通事故的所有车辆都反馈完结果后，再输出结果。
                //  或者 每次事故映射一个 car 的 count 数，等待 count 数到齐以后，再向下游输出结果。
            }
        }).print();

        env.execute();
    }


    private static class CarJoinFunction extends
            CoProcessFunction<CarLocationEvent, Tuple3<Integer, Long, Integer>, Tuple2<Integer, Boolean>> {

        // 注：因为时间戳做了 map 的key，所以同一个 车 同一毫秒只能有一个 位置 或 一个 事故，如果有多个会被覆盖。
        // 如果要考虑同一时间戳多个 event 的情况，需要将 Map 的 value 变为 List。
        private transient MapState<Long, CarLocationEvent> carLocationBuffer;
        private transient MapState<Long, Tuple3<Integer, Long, Integer>> trafficIncidentBuffer;
        // 表示 事故 前 2 s 和 后 1 s ，判断这三秒期间，车辆位置有没有改变。这两个参数可以根据需求调整。
        private final int lowerBound = 2000;
        private final int upperBound = 1000;


        @Override
        public void open(Configuration parameters) {
            carLocationBuffer = getRuntimeContext().getMapState(
                    new MapStateDescriptor<>("carLocationBuffer", Long.class, CarLocationEvent.class));
            trafficIncidentBuffer = getRuntimeContext().getMapState(
                    new MapStateDescriptor<>("trafficIncidentBuffer",
                            Types.LONG, Types.TUPLE(Types.INT, Types.INT, Types.INT)));
        }

        @Override
        public void processElement1(CarLocationEvent event, Context ctx, Collector<Tuple2<Integer, Boolean>> out) throws Exception {
            carLocationBuffer.put(event.timestamp, event);
            // 车辆位置 应该在 3 s 以后被清理，所以定义一个 3 s 以后触发的定时器
            ctx.timerService().registerEventTimeTimer(event.timestamp + lowerBound + upperBound);
        }

        @Override
        public void processElement2(Tuple3<Integer, Long, Integer> event, Context ctx, Collector<Tuple2<Integer, Boolean>> out) throws Exception {
            long timestamp = event.f1;
            trafficIncidentBuffer.put(timestamp, event);
            // 当前车辆 2 s 前的位置信息已经到了，需要在等 1 s，可能还会等到 一些车辆位置信息。所以定义一个 1 s 以后触发的定时器
            ctx.timerService().registerEventTimeTimer(timestamp + upperBound);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<Tuple2<Integer, Boolean>> out) throws Exception {
            // 定时器需要做两个事情：
            // 1. 清理 3 s 以前的车辆位置信息， 事故流不可能关联这些位置信息
            carLocationBuffer.remove(timestamp - lowerBound - upperBound);

            // 2. 查看是否有 故障事件已经等了 1s 了
            Tuple3<Integer, Long, Integer> trafficIncident = trafficIncidentBuffer.get(timestamp - upperBound);
            if (trafficIncident == null) {
                return;
            }
            // 故障事件 删除掉，并检查 是否可信
            trafficIncidentBuffer.remove(timestamp - upperBound);
            // 把 3s 内的当前车辆位置遍历一遍，看是否有移动
            Integer lastLocation = null;
            Long timestampOfTrafficIncident = trafficIncident.f1;
            for (Map.Entry<Long, CarLocationEvent> entry : carLocationBuffer.entries()) {
                Long timestampOfCarLocation = entry.getKey();
                if (timestamp < timestampOfTrafficIncident - lowerBound || timestamp > timestampOfTrafficIncident + upperBound) {
                    // 不在事故区间 3s 以内的位置，直接 过滤
                    continue;
                }

                CarLocationEvent carLocationEvent = entry.getValue();
                if (lastLocation == null) {
                    // 第一次更新位置
                    lastLocation = carLocationEvent.location;
                    continue;
                }

                if (!Objects.equals(carLocationEvent.location, lastLocation)) {
                    System.out.println(String.format("trafficIncident %s 发现 car %s 位置变了", trafficIncident.f0, carLocationEvent.carId));
                    // 位置变了，告诉下游结果
                    out.collect(Tuple2.of(trafficIncident.f0, false));
                    return;
                }

                // 位置没变，继续遍历下一个 位置
            }
            // 所有位置遍历完了，位置没变
            // 可能多个位置确实没变，可能只有一个位置信息，可能一个位置信息也没有。如果后面两者需要特殊处理，可以修改上述 for 循环。
            out.collect(Tuple2.of(trafficIncident.f0, true));
        }
    }


    // Stream A
    public static class CarLocationEvent {

        // event id（主键或 唯一 id）
        private Integer id;

        private Long timestamp;

        private Integer carId;

        private Integer location;

        public CarLocationEvent() {
        }

        public CarLocationEvent(Integer id, Long timestamp, Integer carId, Integer location) {
            this.id = id;
            this.timestamp = timestamp;
            this.carId = carId;
            this.location = location;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Integer getCarId() {
            return carId;
        }

        public void setCarId(Integer carId) {
            this.carId = carId;
        }

        public Integer getLocation() {
            return location;
        }

        public void setLocation(Integer location) {
            this.location = location;
        }
    }

    // Stream B
    public static class TrafficIncidentEvent {

        // event id（主键或 唯一 id）
        private Integer id;

        private Long timestamp;

        private List<Integer> carIds;

        public TrafficIncidentEvent() {
        }

        public TrafficIncidentEvent(Integer id, Long timestamp, List<Integer> carIds) {
            this.id = id;
            this.timestamp = timestamp;
            this.carIds = carIds;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public List<Integer> getCarIds() {
            return carIds;
        }

        public void setCarIds(List<Integer> carIds) {
            this.carIds = carIds;
        }
    }

}
