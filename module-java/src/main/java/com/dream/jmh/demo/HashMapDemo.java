package com.dream.jmh.demo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Thread)
public class HashMapDemo {

  private static final int DATA_SIZE = 500_000;
  private Map<Long, Long> map;
  private AtomicLong counter;

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(HashMapDemo.class.getSimpleName())
        .forks(1)
        .warmupIterations(5)
        .measurementIterations(5)
        .build();

    new Runner(opt).run();
  }

  @Setup
  public void setup() {
    map = new HashMap<>();
    counter = new AtomicLong();
    for (long i = 0; i < DATA_SIZE; i++) {
      map.put(i, i);
    }
  }

  @Benchmark
  public long mapGet() {
    long index = counter.getAndIncrement();
    if(index == Integer.MAX_VALUE){
      counter.set(0);
    }
    long key = index % DATA_SIZE;
    return map.get(key);
  }

}
