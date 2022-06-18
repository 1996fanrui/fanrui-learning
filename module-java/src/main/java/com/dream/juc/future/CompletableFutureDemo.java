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

    public static void main(String[] args) throws InterruptedException {
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

        // test for thenAccept
        // thenAccept 中的 Consumer 会被调用 complete 的线程去执行。
        CompletableFuture<Integer> future1 = new CompletableFuture<>();
        new Thread(() -> future1.complete(1), "Thread aaa").start();
        TimeUnit.MILLISECONDS.sleep(10);
        future1.thenAccept(value -> System.out.println("6 value :" + value + "  ThreadName:" + Thread.currentThread().getName()));

        CompletableFuture<Integer> future2 = new CompletableFuture<>();
        future2.thenAccept(value -> System.out.println("7 value :" + value + "  ThreadName:" + Thread.currentThread().getName()));
        new Thread(() -> future2.complete(2), "Thread bbb").start();
    }

}
