package com.dream.flink.join

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.alibaba.fastjson.JSON
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, FlinkKafkaConsumerBase}
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.ConsumerConfig


/**
  * @author fanrui
  * @time  2020-03-28 00:04:40
  * 场景：一个订单分成了 大订单和小订单，需要在数据流中按照订单 Id 进行匹配，
  * 默认认为数据流的延迟最大为 60s。
  * 大订单和小订单匹配成功后向下游发送，若 60s 还未匹配成功，则测流输出
  * 思路：
  * 提取时间戳，按照 orderId 进行 keyBy，然后两个 流 connect，
  * 大订单和小订单的处理逻辑一样，两个流通过 State 进行关联。
  * 来了一个流，需要保存到自己的状态中，并注册一个 60s 之后的定时器。
  * 如果 60s 内来了第二个流，则将两个数据拼接发送到下游。
  * 如果 60s 内第二个流还没来，就会触发 onTimer，然后进行侧流输出。
  *
  */
object OrderMatch {


    private val KAFKA_CONSUMER_GROUP_ID = "order_match"
    private val JOB_NAME = KAFKA_CONSUMER_GROUP_ID


    val bigOrderTag = new OutputTag[Order]("bigOrder")
    val smallOrderTag = new OutputTag[Order]("smallOrder")

    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
        env.enableCheckpointing(TimeUnit.MINUTES.toMillis(1))

        val checkpointConf = env.getCheckpointConfig
        checkpointConf.setMinPauseBetweenCheckpoints(TimeUnit.SECONDS.toMillis(50))
        checkpointConf.setCheckpointTimeout(TimeUnit.MINUTES.toMillis(6))
        checkpointConf.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
        checkpointConf.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)

        val consumerProps = new Properties()
        consumerProps.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "host:port")
        consumerProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONSUMER_GROUP_ID)
        consumerProps.setProperty(FlinkKafkaConsumerBase.KEY_PARTITION_DISCOVERY_INTERVAL_MILLIS, TimeUnit.MINUTES.toMillis(1).toString)

        val consumerBigOrder = new FlinkKafkaConsumer011[String]("big_order_topic_name",
            new SimpleStringSchema, consumerProps)
                .setStartFromGroupOffsets()

        val consumerSmallOrder = new FlinkKafkaConsumer011[String]("small_order_topic_name",
            new SimpleStringSchema, consumerProps)
                .setStartFromGroupOffsets()

        val bigOrderStream = env.addSource(consumerBigOrder)
                .uid("consumerBigOrder")
                .filter(_ != null)
                .map(json2Order)
                .assignTimestampsAndWatermarks(
                    new BoundedOutOfOrdernessTimestampExtractor[Order](Time.seconds(60)) {
                        override def extractTimestamp(element: Order): Long = {
                            element.time
                        }
                    })
                .keyBy(_.orderId)

        val smallOrderStream = env.addSource(consumerSmallOrder)
                .uid("consumerSmallOrder")
                .filter(_ != null)
                .map(json2Order)
                .assignTimestampsAndWatermarks(
                    new BoundedOutOfOrdernessTimestampExtractor[Order](Time.seconds(60)) {
                        override def extractTimestamp(element: Order): Long = {
                            element.time
                        }
                    })
                .keyBy(_.orderId)

        val result = bigOrderStream.connect(smallOrderStream)
                .process(new OrderMatchProcess)
                .uid("OrderMatchProcess")

        result.print()
        result.getSideOutput(bigOrderTag).print()
        result.getSideOutput(smallOrderTag).print()


        env.execute(JOB_NAME)
    }

    // json To Order 的函数
    def json2Order: String => Order = (json: String) => {
        val jsonObj = JSON.parseObject(json)
        Order(jsonObj.getLongValue("time"), jsonObj.getIntValue("orderId"))
    }

    case class Order(time: Long, orderId: Int)

    class OrderMatchProcess() extends CoProcessFunction[Order, Order, (Order, Order)] {

        lazy val bigState: ValueState[Order] = getRuntimeContext.getState(
            new ValueStateDescriptor[Order]("bigState", classOf[Order]))

        lazy val smallState: ValueState[Order] = getRuntimeContext.getState(
            new ValueStateDescriptor[Order]("smallState", classOf[Order]))

        // 大订单流的处理
        override def processElement1(bigOrder: Order,
                                     ctx: CoProcessFunction[Order, Order, (Order, Order)]#Context,
                                     out: Collector[(Order, Order)]): Unit = {
            val smallOrder = smallState.value()
            // 小订单先来了，直接发送到下游
            if (smallOrder != null) {
                out.collect((bigOrder, smallOrder))
                smallState.clear()
                // todo 这里可以将 Timer 清除
            } else {
                // 小订单还没来，将大订单放到状态中，并注册 1 分钟之后触发的 timer
                bigState.update(bigOrder)
                ctx.timerService().registerEventTimeTimer(bigOrder.time + 60000)
            }
        }

        // 小订单处理逻辑与大订单类似
        override def processElement2(smallOrder: Order,
                                     ctx: CoProcessFunction[Order, Order, (Order, Order)]#Context,
                                     out: Collector[(Order, Order)]): Unit = {
            val bigOrder = bigState.value()
            if (bigOrder != null) {
                out.collect((bigOrder, smallOrder))
                bigState.clear()
                // todo 这里可以将 Timer 清除
            } else {
                smallState.update(smallOrder)
                ctx.timerService().registerEventTimeTimer(smallOrder.time + 60000)
            }
        }

        override def onTimer(timestamp: Long, ctx: CoProcessFunction[Order, Order, (Order, Order)]#OnTimerContext, out: Collector[(Order, Order)]): Unit = {
            // 大订单不为空
            if (bigState.value() != null) {
                ctx.output(bigOrderTag, bigState.value())
            }
            if (smallState.value() != null) {
                ctx.output(smallOrderTag, smallState.value())
            }
            bigState.clear()
            smallState.clear()
        }
    }

}
