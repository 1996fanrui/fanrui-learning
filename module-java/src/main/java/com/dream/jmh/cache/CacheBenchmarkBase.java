package com.dream.jmh.cache;

import com.dream.jmh.BenchmarkBase;
import com.dream.jmh.BenchmarkConstants;
import com.dream.jmh.cache.wrapper.CacheWrapper;
import com.dream.jmh.cache.wrapper.CaffeineCache;
import com.dream.jmh.cache.wrapper.ConcurrentHashMapCache;
import com.dream.jmh.cache.wrapper.GuavaCache;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class CacheBenchmarkBase extends BenchmarkBase {

  @Param({"Map", "Guava", "Caffeine", "GuavaEvict", "CaffeineEvict"})
  private CacheType cacheType;

  @Param({"10", "500000"})
  int setupKeyCount;

  final static BenchmarkConstants constants = new BenchmarkConstants();

  protected CacheWrapper<Long, Long> cache;

  @Setup
  public void setUp() throws Exception {
    constants.constantSetup(setupKeyCount);
    cache = createCache(cacheType);
    loadData();
  }

  private CacheWrapper<Long, Long> createCache(CacheType cacheType) {
    switch (cacheType) {
      case Map:
        return new ConcurrentHashMapCache<>();
      case Guava:
        return new GuavaCache<>();
      case GuavaEvict:
        return new GuavaCache<>(setupKeyCount);
      case Caffeine:
        return new CaffeineCache<>();
      case CaffeineEvict:
        return new CaffeineCache<>(setupKeyCount);
      default:
        throw new IllegalArgumentException("Don't support Cache type: " + cacheType);
    }
  }

  protected void loadData() {
    for (long i = 0; i < setupKeyCount; i++) {
      cache.put(i, i);
    }
  }

  /**
   * KeyValue
   */
  @State(Scope.Thread)
  public static class KeyValue {
    @Setup(Level.Invocation)
    public void kvSetup() {
      int currentIndex = getCurrentIndex();
      setupKey = constants.setupKeys.get(
          currentIndex % constants.setupKeyCount);
    }
    protected long setupKey;
  }

  protected static AtomicInteger keyIndex = new AtomicInteger();
  ;

  private static int getCurrentIndex() {
    int currentIndex = keyIndex.getAndIncrement();
    if (currentIndex == Integer.MAX_VALUE) {
      keyIndex.set(0);
    }
    return currentIndex;
  }

}
