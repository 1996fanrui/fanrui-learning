package com.dream.juc.future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class HeavyOperationImprovement {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService actor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("thread_actor-%d").build());
        ExecutorService heavyPool = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("thread_heavy-%d").build());

        CompletableFuture.runAsync(() -> {
            String input = beforeHeavy();
            CompletableFuture.supplyAsync(() -> heavy(input), heavyPool)
                    .thenAcceptAsync(HeavyOperationImprovement::afterHeavy, actor);
        }, actor);

        TimeUnit.SECONDS.sleep(10);
        actor.shutdownNow();
        heavyPool.shutdownNow();
    }

    private static String beforeHeavy() {
        System.out.println("beforeHeavy         " + Thread.currentThread().getName());
        return "beforeHeavy -> ";
    }

    private static String heavy(String input) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
        System.out.println("Heavy         " + Thread.currentThread().getName());
        return input + " heavy -> ";
    }

    private static void afterHeavy(String input) {
        System.out.println("afterHeavy          " + Thread.currentThread().getName());
    }

}
