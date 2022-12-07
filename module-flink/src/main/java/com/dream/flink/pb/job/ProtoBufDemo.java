package com.dream.flink.pb.job;

import com.dream.flink.pb.Result;
import com.dream.flink.pb.SearchResponse;
import com.twitter.chill.protobuf.ProtobufSerializer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ProtoBufDemo {

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int sleepMs = parameterTool.getInt("sleepMs", 5);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 只需要注册最外层 PB 即可，内层不用管
        env.getConfig().registerTypeWithKryoSerializer(SearchResponse.class, ProtobufSerializer.class);

        env.addSource(
                new ParallelSourceFunction<SearchResponse>() {

                    volatile boolean isCanceled = false;
                    final Random random = new Random();

                    @Override
                    public void run(SourceContext<SearchResponse> ctx) throws Exception {
                        while (!isCanceled) {
                            Result result = Result.newBuilder().setTitle(random.nextInt() + "  title").setUrl(random.nextInt() + "url").build();
                            SearchResponse response = SearchResponse.newBuilder().addResults(result).build();
                            ctx.collect(response);
                        }
                    }

                    @Override
                    public void cancel() {
                        isCanceled = true;
                    }
                })
                .map((MapFunction<SearchResponse, SearchResponse>) value -> value).name("Map___1")
                .rebalance()
                .map((MapFunction<SearchResponse, SearchResponse>) value -> value).name("Map___2")
                .rebalance()
                .map((MapFunction<SearchResponse, SearchResponse>) value -> value).name("Map___3")
                .keyBy(new KeySelector<SearchResponse, Integer>() {
                    @Override
                    public Integer getKey(SearchResponse value) throws Exception {
                        return value.getResultsCount();
                    }
                })
                .reduce(new ReduceFunction<SearchResponse>() {
                    @Override
                    public SearchResponse reduce(SearchResponse value1, SearchResponse value2) throws Exception {
                        return value2;
                    }
                })
                .rebalance()
                .addSink(new RichSinkFunction<SearchResponse>() {
                    @Override
                    public void invoke(SearchResponse value, Context context) throws InterruptedException {
                        List<Result> resultsList = value.getResultsList();
                        System.out.println(resultsList);
                        TimeUnit.MILLISECONDS.sleep(sleepMs);
                    }
                })
                .name("MySink");

        env.execute(ProtoBufDemo.class.getSimpleName());
    }

}
