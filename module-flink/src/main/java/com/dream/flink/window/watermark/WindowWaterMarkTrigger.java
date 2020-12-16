package com.dream.flink.window.watermark;

import com.dream.flink.data.proto.AppInfo;
import com.dream.flink.func.serialize.ProtobufDeserialize;
import com.dream.flink.util.CheckpointUtil;
import com.dream.flink.util.KafkaConfigUtil;
import com.twitter.chill.protobuf.ProtobufSerializer;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase;
import org.apache.flink.util.Collector;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author fanrui
 * @time 2020-01-03 22:57:54
 * @desc 当 窗口大小为 10 毫秒时，请问 WaterMark = 9 还是 10 还是 11 的时候，会触发窗口 [0,10) 进行计算呢？
 * 该案例可以验证出 WaterMark = 9 时，就会触发窗口 [0,10) 进行计算。
 * 源码请参考 InternalTimerServiceImpl 类的 advanceWatermark 方法：
 * public void advanceWatermark(long time) throws Exception {
 * 		currentWatermark = time;
 *
 * 		InternalTimer<K, N> timer;
 *
 * 		while ((timer = eventTimeTimersQueue.peek()) != null && timer.getTimestamp() <= time) {
 * 			eventTimeTimersQueue.poll();
 * 			keyContext.setCurrentKey(timer.getKey());
 * 			triggerTarget.onEventTime(timer);
 * 		}
 * }
 *
 * 重点在于： timer.getTimestamp() <= time
 * 这里 timer.getTimestamp() 表示窗口里的 (endTime-1)，上述案例中就是 9
 * time 表示当前 WaterMark 的时间戳，满足条件就会走下面的触发逻辑，
 * 如果 WaterMark 的 时间戳为 9，9<=9 就会触发逻辑
 *
 *
 */
public class WindowWaterMarkTrigger {


    private static final String KAFKA_CONSUMER_GROUP_ID = "console-consumer-93645";

    private static final String KAFKA_TOPIC = "app-stat";

    private static final String JOB_NAME = KAFKA_CONSUMER_GROUP_ID;


    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);
        env.registerTypeWithKryoSerializer(AppInfo.Log.class, ProtobufSerializer.class);

        CheckpointUtil.setMemoryStateBackend(env);

        FlinkKafkaConsumerBase<AppInfo.Log> consumer = new FlinkKafkaConsumer<>(KAFKA_TOPIC,
                new ProtobufDeserialize<>(AppInfo.Log.class), KafkaConfigUtil.buildConsumerProps(KAFKA_CONSUMER_GROUP_ID))
                .setStartFromLatest();

        env.addSource(consumer)
                .uid(KAFKA_TOPIC)
                .filter(Objects::nonNull)
                .assignTimestampsAndWatermarks(
                        new BoundedOutOfOrdernessTimestampExtractor<AppInfo.Log>
                                (Time.seconds(0)) {
                            @Override
                            public long extractTimestamp(AppInfo.Log element) {
                                return element.getEventTime();
                            }
                        })
                .keyBy(AppInfo.Log::getAppId)
                .timeWindow(Time.milliseconds(10))
                .apply(new WindowFunction<AppInfo.Log, String, String, TimeWindow>() {
                    @Override
                    public void apply(String s, TimeWindow window, Iterable<AppInfo.Log> input, Collector<String> out) throws Exception {
                        Iterator<AppInfo.Log> iterator = input.iterator();
                        String str = "key:" + s + "  window : [" + window.getStart() + ", " + window.getEnd() + ")  data:\n---------\n";
                        while (iterator.hasNext()){
                            AppInfo.Log next = iterator.next();
                            str += next + "---------\n";
                        }
                        out.collect(str);
                    }
                })
                .print();

        env.execute(JOB_NAME);
    }

}
