package com.dream.flink.kryo.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对 LinkedHashMap 增强了 maxSize 的功能
 * @param <K>
 * @param <V>
 */
public class LinkedHashMapEnhance<K, V> extends LinkedHashMap<K, V> {

  private int maxSize;

  public LinkedHashMapEnhance() {
    super();
    maxSize = Integer.MAX_VALUE;
  }

  public LinkedHashMapEnhance(int maxSize) {
    super();
    this.maxSize = maxSize;
  }

  public int getMaxSize() {
    return maxSize;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > maxSize;
  }

  @Override
  public String toString() {
    return "LinkedHashMapEnhance : " + super.toString() + " maxSize:" + maxSize;
  }
}
