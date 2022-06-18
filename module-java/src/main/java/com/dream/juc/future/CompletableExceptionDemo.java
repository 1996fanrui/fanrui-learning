package com.dream.juc.future;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author fanrui
 * @time 2022-02-14 17:06:47
 */
public class CompletableExceptionDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        CompletableFuture<Void> resultFuture = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("2___" + Thread.currentThread().getName());
                throw new IOException("xxxxxx_0000+");
            } catch (Throwable e) {
                throw new CompletionException(e);
            }
        }, executorService)
                .thenApplyAsync(s -> s + "_xxxxx_3___", executorService)
                .thenAcceptAsync(s -> System.out.println(s + "_yyyyy_4___" + Thread.currentThread().getName()));

        resultFuture.handleAsync((ignored, throwable) -> {
            if (throwable != null) {
                // throwable : java.util.concurrent.CompletionException: java.io.IOException: xxxxxx_0000+
                System.out.println("throwable : " + throwable);
            }
            return null;
        }, executorService);

        TimeUnit.SECONDS.sleep(3);
        executorService.shutdown();

        // test case2:
        CompletableFuture<Long> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("completeExceptionally IOException 2."));
        try {
            Long aaa = future.get();
            System.out.println(aaa);
        } catch (Throwable e) {
            // throwable 2 : java.util.concurrent.ExecutionException: java.io.IOException: completeExceptionally IOException 2.
            System.out.println("throwable 2 : " + e);
        }
    }

}
