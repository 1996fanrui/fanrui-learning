package com.dream.juc.future;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author fanrui
 * @time 2022-02-14 17:06:47
 * <p>
 * <p>
 * CompletionStage exceptionally(fn);
 * CompletionStage<R> whenComplete(consumer);
 * CompletionStage<R> whenCompleteAsync(consumer);
 * CompletionStage<R> handle(fn);
 * CompletionStage<R> handleAsync(fn);
 * <p>
 * 1. exceptionally() 方法来处理异常，exceptionally() 的使用非常类似于 try{}catch{}中的 catch{}
 *      如果异常了，exceptionally 返回异常后的结果。
 *      正常的话，下游处理正常的结果，异常的话，下游处理 exceptionally 返回的结果。
 * 2. whenComplete() 和 handle() 系列方法就类似于 try{} catch {} finally{}中的 catch {} finally{}，会拿到 result 或 throwable
 *      当 throwable 为 null，表示没有异常，从 result 里拿结果即可
 *      当 throwable 不为 null，表示有异常，则处理异常
 *
 * 三种方法的应用场景：
 *   如果异常后，需要返回一个默认值 或 根据默认行为生成值，则使用 exceptionally 生成默认值即可。下游继续处理结果即可。
 *   无论是否异常，都需要处理，要么处理 result，要么处理 throwable，则使用 whenComplete 处理即可。下游可以不处理了。
 *   无论是否异常，需要进行一次转换，要么从 result 转换，要么从 throwable 转换，则使用 handle 转换即可。下游继续结果转换的结果即可。
 */
public class CompletableExceptionDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // case 1: 抛出异常后，后续的 apply accept 都不会执行，handle 类似于 catch finally ，会被执行。
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

        // test case2: CompletableFuture 有异常后，future.get 会拿到相应的 Exception
        CompletableFuture<Long> future = new CompletableFuture<>();
        future.completeExceptionally(new IOException("completeExceptionally IOException 2."));
        try {
            Long aaa = future.get();
            System.out.println(aaa);
        } catch (Throwable e) {
            // throwable 2 : java.util.concurrent.ExecutionException: java.io.IOException: completeExceptionally IOException 2.
            System.out.println("throwable 2 : " + e);
        }

        // test 3: supply 里 catch 异常 和  exceptionally 里类似。
        CompletableFuture.supplyAsync(
                () -> {
                    try {
                        System.out.println("3.1___" + Thread.currentThread().getName());
                        throw new RuntimeException("xxxxxx_333333");
                    } catch (Throwable e) {
                        return 0;
                    }
                }, executorService)
                .thenAccept(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        System.out.println("test 3.1 : " + integer);
                    }
                });

        CompletableFuture.supplyAsync(
                (Supplier<Integer>) () -> {
                    System.out.println("3.2___" + Thread.currentThread().getName());
                    throw new RuntimeException("xxxxxx_333333");
                }, executorService)
                .exceptionally((Function<Throwable, Integer>) throwable -> 0)
                .thenAccept(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        System.out.println("test 3.2 : " + integer);
                    }
                });

        executorService.shutdownNow();
    }

}
