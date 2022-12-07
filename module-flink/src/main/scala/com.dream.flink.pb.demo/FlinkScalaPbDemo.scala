package com.dream.flink.pb.demo

import com.twitter.chill.protobuf.ProtobufSerializer
import org.apache.flink.api.common.functions.{MapFunction, ReduceFunction}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}
import org.apache.flink.streaming.api.scala._

import java.util.Random
import java.util.concurrent.TimeUnit


/**
 * Test for scalapb registerTypeWithKryoSerializer
 *
 * Conclusion: the kryo serializer doesn't take effect for scalapb, java pb works well.
 *
 * scalapb will use the org.apache.flink.api.scala.typeutils.ScalaCaseClassSerializer directly.
 *
 * code:
 * 1. we can see the serializer in the StreamGraph.setSerializers method.
 * 2. ExecutionConfig.registeredTypesWithKryoSerializers doesn't be called
 *
 * Person copy from : https://github.com/thesamet/scalapb-maven-example
 */
object FlinkScalaPbDemo {

    def main(args: Array[String]): Unit = {

        val array = Person.of("aaa", 1).toByteArray
        val person = Person.parseFrom(array)
        println(person)

        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.getConfig.registerTypeWithKryoSerializer(classOf[Person], classOf[ProtobufSerializer])

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
        })
          .map(person => person).name("Map___1")
          .rebalance
          .map(person => person).name("Map___2")
          .rebalance
          .map(person => person).name("Map___3")
          .keyBy(new KeySelector[Person, Integer]() {
            @throws[Exception]
            override def getKey(value: Person): Integer = {
                value.age
            }
        }).reduce(new ReduceFunction[Person]() {
            @throws[Exception]
            override def reduce(value1: Person, value2: Person): Person = {
                return value2
            }
        }).rebalance.addSink(new RichSinkFunction[Person]() {
            @throws[InterruptedException]
            override def invoke(value: Person, context: SinkFunction.Context): Unit = {
                System.out.println(value)
                //                        if (getRuntimeContext().getIndexOfThisSubtask() == 0) {
                TimeUnit.MILLISECONDS.sleep(10)
                //                        }
            }
        }).name("MySink")
        env.execute("scala_job")

    }


}
