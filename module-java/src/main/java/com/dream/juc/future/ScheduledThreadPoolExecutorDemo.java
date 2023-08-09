package com.dream.juc.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledThreadPoolExecutorDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        AtomicLong counter = new AtomicLong();
        long startTime = System.currentTimeMillis();
        final int expectedCount = 10_000_000;
        CompletableFuture<Void> future = new CompletableFuture<>();
        for (int i = 0; i < expectedCount; i++) {
            executor.schedule(() -> {
                if (counter.incrementAndGet() == expectedCount) {
                    future.complete(null);
                }
            }, 1, TimeUnit.SECONDS);
        }
        System.out.println("Schedule duration: " + (System.currentTimeMillis() - startTime));

        future.get();
        System.out.println("Completed duration: " + (System.currentTimeMillis() - startTime));

        executor.shutdownNow();
    }
}
