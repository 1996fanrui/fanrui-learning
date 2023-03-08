package com.dream.flink.io.highio;

import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * java -cp aa610a92462a4de587c7d9e8a70cf8e8 com.dream.flink.io.highio.HighCPU --threadCount 20 --durationMinutes 10
 *
 * 验证有一个线程访问 io 的场景，当前进程是否会影响？
 */
public class HighCPU {

    private static final int DEFAULT_THREAD_COUNT = 5;
    private static final int DEFAULT_DURATION_MINUTES = 2;

    public static void main(String[] args) throws InterruptedException, IOException {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int threadCount = parameterTool.getInt("threadCount", DEFAULT_THREAD_COUNT);
        int durationMinutes = parameterTool.getInt("durationMinutes", DEFAULT_DURATION_MINUTES);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new CPUTask());
        }
        TimeUnit.MINUTES.sleep(durationMinutes);
        executor.shutdownNow();

        // clean up the work dir.
    }

    private static class CPUTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
