package com.dream.flink.pb.demo.non

import com.dream.flink.pb.demo.{Counter, Person}
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}

class ValueStateMapFunction extends RichMapFunction[Counter, Person] {
  // state 里会使用 Kryo 序列化，应为 getState 会调用 Java env
    lazy val person: ValueState[Person] = getRuntimeContext.getState(
      new ValueStateDescriptor[Person]("person", classOf[Person]))

  override def map(counter: Counter): Person = {
    val oldPerson = person.value()
    val newPerson = counter.getPerson
    if (oldPerson != null) {
      newPerson.withName(oldPerson.name)
    }
    person.update(newPerson)
    newPerson
  }
}
