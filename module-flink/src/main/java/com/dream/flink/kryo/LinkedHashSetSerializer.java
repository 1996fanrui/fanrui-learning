package com.dream.flink.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fanrui03
 * @param <K>
 * @param <V>
 */
public class LinkedHashSetSerializer<K, V> extends Serializer<LinkedHashMapEnhance<K, V>> implements Serializable {

  private static final long serialVersionUID = -3335512745506751743L;

  @Override
  public void write(Kryo kryo, Output output, LinkedHashMapEnhance<K, V> linkedHashSet) {
    kryo.writeObject(output, linkedHashSet.size());
    kryo.writeObject(output, linkedHashSet.getMaxSize());

    for (Map.Entry<K, V> entry : linkedHashSet.entrySet()) {
      kryo.writeClassAndObject(output, entry.getKey());
      kryo.writeClassAndObject(output, entry.getValue());
    }
  }

  @Override
  public LinkedHashMapEnhance<K, V> read(Kryo kryo, Input input, Class<LinkedHashMapEnhance<K, V>> aClass) {
    int size = kryo.readObject(input, Integer.class);
    int maxSize = kryo.readObject(input, Integer.class);
    LinkedHashMapEnhance<K, V> result = new LinkedHashMapEnhance<>(maxSize);

    for (int i = 0; i < size; i++) {
      K key = (K) kryo.readClassAndObject(input);
      V value = (V) kryo.readClassAndObject(input);
      result.put(key, value);
    }
    return result;
  }
}
