package com.dream.jmh.map;

import com.dream.jmh.BenchmarkBase;
import com.dream.jmh.BenchmarkConstants;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MapBenchmarkBase extends BenchmarkBase {

    protected Map<Long, Long> map;

    @Param({"500000"})
    int setupKeyCount;

    final static BenchmarkConstants constants = new BenchmarkConstants();

    @Setup
    public void setUp() throws Exception {
        keyIndex = new AtomicInteger();
        constants.constantSetup(setupKeyCount);
        doSetUp();
    }

    abstract void doSetUp() throws Exception;

    @Benchmark
    public Long valueGet(KeyValue keyValue) throws IOException {
        return map.get(keyValue.setupKey);
    }

    @Benchmark
    public boolean containsKey(KeyValue keyValue) throws IOException {
        return map.containsKey(keyValue.setupKey);
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

    protected static AtomicInteger keyIndex;

    private static int getCurrentIndex() {
        int currentIndex = keyIndex.getAndIncrement();
        if (currentIndex == Integer.MAX_VALUE) {
            keyIndex.set(0);
        }
        return currentIndex;
    }

}
