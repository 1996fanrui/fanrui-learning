package com.dream.jmh.cache.wrapper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class GuavaCache<K, V> implements CacheWrapper<K, V> {

  private Cache<K, V> cache;

  public GuavaCache() {
    this.cache = CacheBuilder.newBuilder()
        .build();
  }

  public GuavaCache(long maximumSize) {
    this.cache = CacheBuilder.newBuilder()
        .maximumSize(maximumSize)
        .expireAfterWrite(12, TimeUnit.HOURS)
        .build();
  }

  @Override
  public void put(K key, V value) {
    cache.put(key, value);
  }

  @Override
  public V get(K key) {
    return cache.getIfPresent(key);
  }
}
