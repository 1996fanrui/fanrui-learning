package com.dream.flink.uc;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.streaming.api.functions.sink.v2.DiscardingSink;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * FLINK-35051: Reproduce the UC doesn't work well with Async Operator.
 */
public class AsyncFunctionWithUC {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncFunctionWithUC.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        conf.setString("execution.checkpointing.unaligned.enabled", "true");
        conf.setString("execution.checkpointing.interval", "10s");
        conf.setString("execution.checkpointing.min-pause", "8s");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        env.setParallelism(1);

        DataStream<String> source = env.fromSource(
                        new DataGeneratorSource<>(
                                value -> new RandomDataGenerator().nextHexString(300),
                                Long.MAX_VALUE,
                                RateLimiterStrategy.perSecond(100000),
                                Types.STRING), WatermarkStrategy.noWatermarks(), "Source Task");

        AsyncDataStream.orderedWait(
                        source,
                        new MyAsyncFunction(), 2, TimeUnit.SECONDS, 1000)
//                source
                .flatMap(new AmplificationAndSleep())
                .rebalance()
                .flatMap(new AmplificationAndSleep(10, false))
                .sinkTo(new DiscardingSink<>());

        env.execute(AsyncFunctionWithUC.class.getSimpleName());
    }

    private static class AmplificationAndSleep<V> implements FlatMapFunction<V, V> {

        private final int factor;
        private final boolean print;

        public AmplificationAndSleep() {
            this(10, true);
        }

        public AmplificationAndSleep(int factor, boolean print) {
            this.factor = factor;
            this.print = print;
        }

        @Override
        public void flatMap(V value, Collector<V> out) throws Exception {
            if (print) {
                LOG.info("flatMap");
            }
            for (int i = 0; i < factor; i++) {
                Thread.sleep(1);
                out.collect(value);
            }
        }
    }

    private static class MyAsyncFunction extends RichAsyncFunction<String, String> {

        ExecutorService executor;

        @Override
        public void open(OpenContext openContext) {
            this.executor = Executors.newFixedThreadPool(1000);
        }

        @Override
        public void asyncInvoke(String value, ResultFuture<String> resultFuture) {
            CompletableFuture.supplyAsync(
                            () -> {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(100));
                                } catch (InterruptedException exception) {
                                    throw new RuntimeException(exception);
                                }
                                return value;
                            }, executor)
                    .whenCompleteAsync((result, throwable) -> {
                        if (result != null) {
                            resultFuture.complete(Collections.singleton(result));
                        } else {
                            resultFuture.complete(Collections.emptyList());
                        }
                    }, executor);
        }

        @Override
        public void timeout(String value, ResultFuture<String> resultFuture) {}

        @Override
        public void close() {
            executor.shutdown();
        }
    }
}