package com.dream.flink.graph;

import com.dream.flink.data.Order;
import com.dream.flink.data.OrderGenerator;
import com.dream.flink.util.CheckpointUtil;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author fanrui03
 * @time 2020-09-02 11:15:42
 * https://flink.apache.org/visualizer/
 */
public class ExecutionPlan {

  private static final String JOB_NAME = "hdp-operator-state-demo";

  public static void main(String[] args) throws Exception {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    env.setParallelism(300);
    CheckpointUtil.setConfYamlStateBackend(env);

    env.addSource(new OrderGenerator())
        .keyBy(Order::getUserId)
        .map(new MapFunction<Order, String>() {
          @Override
          public String map(Order value) throws Exception {
            return value.getOrderId();
          }
        })
        .setParallelism(60)
        .print()
        .setParallelism(60);

    System.out.println(env.getExecutionPlan());
  }


}
