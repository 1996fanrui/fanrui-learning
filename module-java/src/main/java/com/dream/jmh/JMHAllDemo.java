package com.dream.jmh;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * @author fanrui03
 * @time 2020-06-02 17:25:59
 * ref: https://blog.dyngr.com/blog/2016/10/29/introduction-of-jmh/
 * desc：
 * 测试单线程和多线程计算加法的耗时情况，
 * 用到了 JMH 的 Param、Setup 和 TearDown 注解
 * <p>
 * Benchmark 结果：
 * # Run complete. Total time: 00:02:08
 * <p>
 * Benchmark                     (length)  Mode  Cnt    Score    Error  Units
 * JMHAllDemo.multiThreadBench      10000  avgt   10   12.862 ±  0.466  us/op
 * JMHAllDemo.multiThreadBench     100000  avgt   10   25.335 ±  0.899  us/op
 * JMHAllDemo.multiThreadBench    1000000  avgt   10   86.069 ±  3.369  us/op
 * JMHAllDemo.singleThreadBench     10000  avgt   10    2.480 ±  0.144  us/op
 * JMHAllDemo.singleThreadBench    100000  avgt   10   25.681 ±  1.186  us/op
 * JMHAllDemo.singleThreadBench   1000000  avgt   10  388.175 ± 38.619  us/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class JMHAllDemo {

    // Benchmark 时会把各种参数全跑一遍
    @Param({"10000", "100000", "1000000"})
    private int length;

    private int[] numbers;
    private Calculator singleThreadCalc;
    private Calculator multiThreadCalc;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHAllDemo.class.getSimpleName())
                .forks(2)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public long singleThreadBench() {
        return singleThreadCalc.sum(numbers);
    }

    @Benchmark
    public long multiThreadBench() {
        return multiThreadCalc.sum(numbers);
    }

    @Setup
    public void prepare() {
        numbers = IntStream.rangeClosed(1, length).toArray();
        singleThreadCalc = new SinglethreadCalculator();
        multiThreadCalc = new MultithreadCalculator(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void shutdown() {
        singleThreadCalc.shutdown();
        multiThreadCalc.shutdown();
    }


    public interface Calculator {
        /**
         * calculate sum of an integer array
         *
         * @param numbers
         * @return
         */
        public long sum(int[] numbers);

        /**
         * shutdown pool or reclaim any related resources
         */
        public void shutdown();
    }

    public class SinglethreadCalculator implements Calculator {
        public long sum(int[] numbers) {
            long total = 0L;
            for (int i : numbers) {
                total += i;
            }
            return total;
        }

        @Override
        public void shutdown() {
            // nothing to do
        }
    }

    public class MultithreadCalculator implements Calculator {
        private final int nThreads;
        private final ExecutorService pool;

        public MultithreadCalculator(int nThreads) {
            this.nThreads = nThreads;
            this.pool = Executors.newFixedThreadPool(nThreads);
        }

        private class SumTask implements Callable<Long> {
            private int[] numbers;
            private int from;
            private int to;

            public SumTask(int[] numbers, int from, int to) {
                this.numbers = numbers;
                this.from = from;
                this.to = to;
            }

            public Long call() throws Exception {
                long total = 0L;
                for (int i = from; i < to; i++) {
                    total += numbers[i];
                }
                return total;
            }
        }

        public long sum(int[] numbers) {
            int chunk = numbers.length / nThreads;

            int from, to;
            List<SumTask> tasks = new ArrayList<SumTask>();
            for (int i = 1; i <= nThreads; i++) {
                if (i == nThreads) {
                    from = (i - 1) * chunk;
                    to = numbers.length;
                } else {
                    from = (i - 1) * chunk;
                    to = i * chunk;
                }
                tasks.add(new SumTask(numbers, from, to));
            }

            try {
                List<Future<Long>> futures = pool.invokeAll(tasks);

                long total = 0L;
                for (Future<Long> future : futures) {
                    total += future.get();
                }
                return total;
            } catch (Exception e) {
                // ignore
                return 0;
            }
        }

        @Override
        public void shutdown() {
            pool.shutdown();
        }
    }
}
