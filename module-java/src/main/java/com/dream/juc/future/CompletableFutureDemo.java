package com.dream.juc.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @time 2020-05-17 12:59:16
 */
public class CompletableFutureDemo {

    public static void main(String[] args) {
        System.out.println("1" + Thread.currentThread().getName());

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
                        System.out.println("2" + Thread.currentThread().getName()),
                executorService)
                .thenRun(() -> {
                            System.out.println("3" + Thread.currentThread().getName());
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                )
                .thenRunAsync(() -> {
                            System.out.println("4" + Thread.currentThread().getName());
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                )
                .thenRunAsync(() -> {
                            System.out.println("5" + Thread.currentThread().getName());
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                );

        voidCompletableFuture.join();
        executorService.shutdown();

    }

}
