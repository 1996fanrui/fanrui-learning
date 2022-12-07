package com.dream.flink.pb.demo

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.java.typeutils.GenericTypeInfo
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}

import java.util.Random
import java.util.concurrent.TimeUnit

/**
 * Person copy from : https://github.com/thesamet/scalapb-maven-example
 *
 * Solution:
 * 1. Using the java env instead of the scala env.
 * 2. Set the `new GenericTypeInfo(classOf[Person])` as the type info.
 *
 */
object FlinkScalaPbDemoWithJavaEnv {

    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.getConfig.registerTypeWithKryoSerializer(classOf[Person], classOf[ScalaProtobufSerializer])

        env.addSource(new ParallelSourceFunction[Person]() {
            private var isCanceled: Boolean = false
            final private val random: Random = new Random@throws[Exception]

            override def run(ctx: SourceFunction.SourceContext[Person]): Unit = {
                while ( {
                    !(isCanceled)
                }) {
                    val person: Person = Person.of(random.nextInt + "  title", random.nextInt)
                    ctx.collect(person)
                }
            }

            override def cancel(): Unit = {
                isCanceled = true
            }

        }, new GenericTypeInfo(classOf[Person]))
          .rebalance()
          .map(new MapFunction[Person, Person]() {
              override def map(value: Person): Person = value
          })
          .rebalance()
          .map(new MapFunction[Person, Person]() {
              override def map(value: Person): Person = value
          })
          .rebalance.addSink(new RichSinkFunction[Person]() {
            @throws[InterruptedException]
            override def invoke(value: Person, context: SinkFunction.Context): Unit = {
                System.out.println(value)
                TimeUnit.MILLISECONDS.sleep(1000)
            }
        }).name("MySink")
        env.execute("scala_job")

    }
}
