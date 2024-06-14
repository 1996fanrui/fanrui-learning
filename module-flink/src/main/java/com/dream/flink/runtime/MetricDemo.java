/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.flink.runtime;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Test for Metric with slf4j reporter.
 */
public class MetricDemo {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set(RestOptions.PORT, 8081);

        conf.setString("metrics.reporters", "slf4j");
        conf.setString("metrics.reporter.slf4j.factory.class", "org.apache.flink.metrics.slf4j.Slf4jReporterFactory");
        conf.setString("metrics.reporter.slf4j.interval", "10 SECONDS");

        conf.setString("metrics.latency.granularity", "single");
        conf.setString("metrics.latency.interval", "1000");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(conf);
        env.setParallelism(2);
        env.enableCheckpointing(5_000);

        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);

        DataGeneratorSource<Long> generatorSource =
                new DataGeneratorSource<>(
                        value -> value,
                        600,
                        RateLimiterStrategy.perSecond(10),
                        Types.LONG);

        env.fromSource(generatorSource, WatermarkStrategy.noWatermarks(), "Data Generator")
                .rebalance()
                .map(new ExceptionableMapper<>())
                .name("Map___1")
                .print();

        env.execute(MetricDemo.class.getSimpleName());
    }

    public static class ExceptionableMapper<T> implements MapFunction<T, T>, CheckpointedFunction {

        @Override
        public T map(T value) throws Exception {
            return value;
        }

        @Override
        public void snapshotState(FunctionSnapshotContext context) {
            if (context.getCheckpointId() == 5) {
                throw new RuntimeException();
            }
        }

        @Override
        public void initializeState(FunctionInitializationContext context) {
        }
    }

}
