package com.dream.flink.kryo.map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.Serializable;
import java.util.Map;

/**
 * @param <K>
 * @param <V>
 * @author fanrui03
 */
public class LinkedHashSetSerializer<K, V> extends Serializer<LinkedHashMapEnhance<K, V>> implements Serializable {

  private static final long serialVersionUID = -3335512745506751743L;

  @Override
  public void write(Kryo kryo, Output output, LinkedHashMapEnhance<K, V> linkedHashMapEnhance) {
    // 序列化 map 的容量和 maxSize
    kryo.writeObject(output, linkedHashMapEnhance.size());
    kryo.writeObject(output, linkedHashMapEnhance.getMaxSize());

    // 迭代器遍历一条条数据，将其序列化写出到 output
    for (Map.Entry<K, V> entry : linkedHashMapEnhance.entrySet()) {
      kryo.writeClassAndObject(output, entry.getKey());
      kryo.writeClassAndObject(output, entry.getValue());
    }
  }

  @Override
  public LinkedHashMapEnhance<K, V> read(Kryo kryo, Input input, Class<LinkedHashMapEnhance<K, V>> aClass) {
    // 先读取 map 的容量和 maxSize
    int size = kryo.readObject(input, Integer.class);
    int maxSize = kryo.readObject(input, Integer.class);
    // 构造 LinkedHashMapEnhance
    LinkedHashMapEnhance<K, V> map = new LinkedHashMapEnhance<>(maxSize);

    // for 循环遍历 size 次，每次读取出 key 和 value，并将其插入到 map 中
    for (int i = 0; i < size; i++) {
      K key = (K) kryo.readClassAndObject(input);
      V value = (V) kryo.readClassAndObject(input);
      map.put(key, value);
    }
    return map;
  }
}
