package com.dream.flink.window.watermark;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Using ProcessingTime under EventTime TimeCharacteristic.
 *
 * Check whether the ProcessingTime Window can be triggered when there is very little data? Yes, it can be triggered.
 *
 * 每次第 42 秒的时候，产生一条数据，分钟整点时，出触发窗口计算。
 * 2022-08-08 11:04:42  send data to downstream.
 * 2022-08-08 11:05:00       pv : 1
 * 2022-08-08 11:05:42  send data to downstream.
 * 2022-08-08 11:06:00       pv : 1
 *
 */
public class WindowProcessTimeTrigger {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.addSource(new SourceFunction<Long>() {

            private volatile boolean isRunning = true;

            private long lastTimestamp = 0;

            @Override
            public void run(SourceContext<Long> ctx) throws Exception {
                while (isRunning) {
                    long currentTimestamp = System.currentTimeMillis();
                    if (currentTimestamp - lastTimestamp > TimeUnit.SECONDS.toMillis(60)) {
                        this.lastTimestamp = currentTimestamp;
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(new Date());
                        System.out.println(dateString + "  send data to downstream.");
                        ctx.collect(1L);
                    }
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        })
                .keyBy((KeySelector<Long, Long>) value -> value)
                .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
                .reduce((ReduceFunction<Long>) Long::sum)
                .addSink(new SinkFunction<Long>() {
                    @Override
                    public void invoke(Long pv, Context context) throws Exception {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(new Date());
                        System.out.println(dateString + "       pv : " + pv);
                    }
                });

        env.execute(WindowProcessTimeTrigger.class.getSimpleName());
    }

}
