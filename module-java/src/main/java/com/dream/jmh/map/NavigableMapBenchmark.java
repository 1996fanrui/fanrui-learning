package com.dream.jmh.map;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class NavigableMapBenchmark extends MapBenchmarkBase {

    @Param({"TreeMap", "SkipList"})
    private MapType mapType;

    private NavigableMap<Long, Long> navigableMap;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + NavigableMapBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

    @Override
    void doSetUp() throws Exception {
        navigableMap = createMap(mapType);
        map = navigableMap;
        loadData();
    }

    private NavigableMap<Long, Long> createMap(MapType backendType) {
        switch (backendType) {
            case SkipList:
                return new ConcurrentSkipListMap<>();
            case TreeMap:
                return new TreeMap<>();
            default:
                throw new IllegalArgumentException("Don't support Map type: " + backendType);
        }
    }

    protected void loadData() {
        for (long i = 0; i < setupKeyCount; i++) {
            navigableMap.put(i, i);
        }
    }

    @Benchmark
    public Long valueIteratorGet(KeyValue keyValue) throws IOException {
        Iterator<Long> iterator = navigableMap.tailMap(keyValue.setupKey).values().iterator();
        return iterator.next();
    }

    @Benchmark
    public Long valueIterator(KeyValue keyValue) throws IOException {
        Iterator<Long> iterator = navigableMap.tailMap(keyValue.setupKey).values().iterator();
        int i = 0;
        long result = 0;
        while (iterator.hasNext() && i++ < 5000) {
            result = iterator.next();
        }
        return result;
    }

}
