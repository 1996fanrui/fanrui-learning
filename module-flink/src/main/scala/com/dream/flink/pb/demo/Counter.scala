package com.dream.flink.pb.demo

// 注：当 Pojo 里是 private 变量结合 setter getter 方法赋值时，Flink 会把 pb 当做 GenericType
// 当 Pojo 里使用 public 变量时，Flink 会把 pb 当做 Scala case class
class Counter {
  private var value: Int = _

  private var person: Person = _

  def setValue(value: Int): Unit = {
    this.value = value
  }

  def getValue: Int = {
    value
  }

  def setPerson(person: Person): Unit = {
    this.person = person
  }

  def getPerson: Person = {
    person
  }

  // bad case, 这种写法会把 Person protobuf 当做 CaseClass 来处理
  //  var value : Int = _
  //  var person: Person = _
}
