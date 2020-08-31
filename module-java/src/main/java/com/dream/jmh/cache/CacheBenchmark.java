package com.dream.jmh.cache;

import com.dream.jmh.map.MapBenchmarkImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.IOException;

public class CacheBenchmark extends CacheBenchmarkBase {

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .verbosity(VerboseMode.NORMAL)
        .include(".*" + MapBenchmarkImpl.class.getSimpleName() + ".*")
        .build();

    new Runner(opt).run();
  }

  @Benchmark
  public Long valueGet(KeyValue keyValue) throws IOException {
    return cache.get(keyValue.setupKey);
  }

  @Benchmark
  public void valuePut(KeyValue keyValue) throws IOException {
    cache.put(keyValue.setupKey, keyValue.setupKey);
  }

}
