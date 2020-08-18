package com.dream.jmh.map;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;

public class MapBenchmarkImpl extends MapBenchmarkBase {

    @Param({"HashMap", "ConcurrentHashMap"})
    private MapType mapType;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + MapBenchmarkImpl.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

    @Override
    void doSetUp() throws Exception {
        map = createMap(mapType);
        loadData();
    }

    private Map<Long, Long> createMap(MapType mapType) {
        switch (mapType) {
            case HashMap:
                return new HashMap<>();
            case ConcurrentHashMap:
                return new ConcurrentHashMap<>();
            default:
                throw new IllegalArgumentException("Don't support Map type: " + mapType);
        }
    }

    protected void loadData() {
        for (long i = 0; i < setupKeyCount; i++) {
            map.put(i, i);
        }
    }

}
