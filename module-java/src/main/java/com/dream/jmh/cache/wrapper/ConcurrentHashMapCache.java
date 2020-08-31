package com.dream.jmh.cache.wrapper;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCache<K, V> implements CacheWrapper<K, V> {

  private ConcurrentHashMap<K, V> map;

  public ConcurrentHashMapCache() {
    this.map = new ConcurrentHashMap<>();
  }

  @Override
  public void put(K key, V value) {
    map.put(key, value);
  }

  @Override
  public V get(K key) {
    return map.get(key);
  }

}