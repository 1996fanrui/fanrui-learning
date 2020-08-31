package com.dream.jmh.cache.wrapper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineCache<K, V> implements CacheWrapper<K, V> {

  private Cache<K, V> cache;

  public CaffeineCache() {
    this.cache = Caffeine.newBuilder()
        .build();
  }

  public CaffeineCache(long maximumSize) {
    this.cache = Caffeine.newBuilder()
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
