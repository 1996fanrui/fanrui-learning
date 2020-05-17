package com.dream.juc.future;

import java.util.concurrent.*;

/**
 * @author fanrui03
 * @time 2020-05-17 12:54:14
 *
 */
public class FutureTaskDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            System.out.println("Callable 中。。。");
            TimeUnit.SECONDS.sleep(2);
            return 666;
        });

        new Thread(futureTask).start();

        System.out.println("线程是否执行结束：" + futureTask.isDone());
        // get 阻塞获取结果
        System.out.println(futureTask.get());
        System.out.println("线程是否执行结束：" + futureTask.isDone());
    }

}
