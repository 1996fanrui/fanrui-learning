package com.dream.flink.pb.demo.non

import com.dream.flink.pb.demo.{Counter, Person, ScalaProtobufSerializer}
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala._

import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * Person copy from : https://github.com/thesamet/scalapb-maven-example
 *
 * Solution:
 * Using the Pojo instead of scala case class.
 * Pojo includes some protobuf and base types, and it will use the KryoSerializer as the serializer for protobuf.
 *
 * 案例中：Counter 里包装一个 pb 是没问题的，可以走 pojo + pb 序列化。
 * 问题： 如果两个 Task 之间传输的是 单纯的 pb，仍然有问题，会走 scala case class 的序列化器。
 *
 * Function 里的 state 用 pb 没问题，因为 getState 会调用 Java 方法，会把 Scala 类转为 GenericType
 */
object FlinkScalaPbDemoWithNonCaseClass {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.registerTypeWithKryoSerializer(classOf[Person], classOf[ScalaProtobufSerializer])

    env.addSource(new ParallelSourceFunction[Counter]() {
      private var isCanceled: Boolean = false
      final private val random: Random = new Random@throws[Exception]

      override def run(ctx: SourceFunction.SourceContext[Counter]): Unit = {
        while ( {
          !isCanceled
        }) {
          val counter: Counter = new Counter
          counter.setValue(random.nextInt())
          counter.setPerson(Person.of(random.nextInt() + "xxxx", 2))
          ctx.collect(counter)
        }
      }

      override def cancel(): Unit = {
        isCanceled = true
      }
    })
      .map(counter => counter).name("Map___1")
      .rebalance
      .map(counter => counter).name("Map___2")
      .keyBy(counter => counter.getValue)
      .map(new ValueStateMapFunction)
      .rebalance
      .addSink(new RichSinkFunction[Person]() {
        @throws[InterruptedException]
        override def invoke(value: Person, context: SinkFunction.Context): Unit = {
          println(value.name + "       xxxxxx        " + value.age)
          TimeUnit.MILLISECONDS.sleep(10)
        }
      }).name("MySink")
    env.execute("scala_job")

  }


}
