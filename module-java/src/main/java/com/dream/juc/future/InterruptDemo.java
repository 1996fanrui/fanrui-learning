package com.dream.juc.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class InterruptDemo {

    public static void main(String[] args) throws Exception {
        // test for multi InterruptedException
        CompletableFuture<Long> future1 = new CompletableFuture<>();
        CompletableFuture<Long> future2 = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            try {
                future1.get();
            } catch (Exception e) {
                // throwable 3_1 : java.lang.InterruptedException
                System.out.println("throwable 3_1 : " + e);
                // 下一次 get 时，也会 InterruptedException
                Thread.currentThread().interrupt();
            }

            try {
                future2.get();
            } catch (Exception e) {
                // throwable 3_2 : java.lang.InterruptedException
                System.out.println("throwable 3_2 : " + e);
            }

        });

        thread.start();
        TimeUnit.MILLISECONDS.sleep(10);
        thread.interrupt();
        thread.join();
    }
}
