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
                System.out.println("throwable : " + throwable);
            }
            return null;
        }, executorService);

        TimeUnit.SECONDS.sleep(3);
        executorService.shutdown();
    }

}
