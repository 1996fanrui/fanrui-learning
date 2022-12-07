package com.dream.flink.pb.demo

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import scalapb.GeneratedMessage

import java.lang.reflect.Method
import java.util



class ScalaProtobufSerializer extends Serializer[GeneratedMessage] {

    override def write(kryo: Kryo, output: Output, mes: GeneratedMessage): Unit = {
        val ser: Array[Byte] = mes.toByteArray
        output.writeInt(ser.length, true)
        output.writeBytes(ser)
    }

    override def read(kryo: Kryo, input: Input, pbClass: Class[GeneratedMessage]): GeneratedMessage = try {
        val size: Int = input.readInt(true)
        val barr: Array[Byte] = new Array[Byte](size)
        input.readBytes(barr)
        getParse(pbClass).invoke(null, barr).asInstanceOf[GeneratedMessage]
    } catch {
        case e: Exception =>
            throw new RuntimeException("Could not create " + pbClass, e)
    }

    /* This cache never clears, but only scales like the number of
   * classes in play, which should not be very large.
   * We can replace with a LRU if we start to see any issues.
   */
    protected val methodCache: util.HashMap[Class[_], Method] = new util.HashMap[Class[_], Method]

    /**
     * This is slow, so we should cache to avoid killing perf:
     * See: http://www.jguru.com/faq/view.jsp?EID=246569
     */
    @throws[Exception]
    protected def getParse(cls: Class[_]): Method = {
        var meth: Method = methodCache.get(cls)
        if (null == meth) {
            meth = cls.getMethod("parseFrom", classOf[Array[Byte]])
            methodCache.put(cls, meth)
        }
        meth
    }
}