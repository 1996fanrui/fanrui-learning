package com.dream.flink.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;

import io.lettuce.core.RedisClient;

/**
 * cd ./docker/redis
 * docker-compose -f docker-compose-redis-only.yml up
 * docker exec -it test-redis redis-cli
 * SET 0 "Beijing"
 * SET 1 "Shanghai"
 * SET 2 "Guangzhou"
 * SET 3 "Shenzhen"
 * SET 4 "Hangzhou"
 * SET 5 "Nanjing"
 * SET 6 "Chengdu"
 * SET 7 "Wuhan"
 * SET 8 "Tianjin"
 * SET 9 "Foshan"
 * docker-compose -f docker-compose-redis-only.yml down
 *
 */
public class LookupJoinRedisDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.setString("rest.flamegraph.enabled","true");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);

        env.setParallelism(5);

        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        DataGeneratorSource<Tuple4<Long, Integer, String, Double>> generatorSource =
                new DataGeneratorSource<>(
                        id -> {
                            int cityId = randomDataGenerator.nextInt(0, 9);
                            String userName = randomDataGenerator.nextHexString(10);
                            double price = randomDataGenerator.nextUniform(0, 100);
                            return Tuple4.of(id, cityId, userName, price);
                        },
                        Long.MAX_VALUE,
                        RateLimiterStrategy.perSecond(200000),
                        TypeInformation.of(
                                new TypeHint<Tuple4<Long, Integer, String, Double>>() {}));

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator").setParallelism(2)
                .rebalance()
                .map(new LookupJoinRedisMapper())
//                .print();
                .addSink(new DiscardingSink<>()).name("MySink").setParallelism(2);

        env.execute(LookupJoinRedisDemo.class.getSimpleName());
    }

    private static class LookupJoinRedisMapper extends RichMapFunction<
            Tuple4<Long, Integer, String, Double>, Tuple5<Long, Integer, String, Double, String>> {

        private RedisClient redisClient;
        private  StatefulRedisConnection<String, String> connection;

        @Override
        public void open(Configuration parameters) throws Exception {
            redisClient = RedisClient.create("redis://password@localhost:6379/");
            connection = redisClient.connect();
        }

        @Override
        public Tuple5<Long, Integer, String, Double, String> map(
                Tuple4<Long, Integer, String, Double> tuple4) throws Exception {
            String cityName = getCityNameFromRedis(tuple4.f1);
            return Tuple5.of(tuple4.f0, tuple4.f1, tuple4.f2, tuple4.f3, cityName) ;
        }

        private String getCityNameFromRedis(int cityId) {
            RedisCommands<String, String> sync = connection.sync();
            return sync.get(Integer.toString(cityId));
        }

        @Override
        public void close() throws Exception {
            redisClient.close();
            connection.close();
        }
    }
}
