package com.dream.guava.cache;

import com.google.common.base.Preconditions;

import java.util.concurrent.*;

/**
 * @author fanrui03
 * @time 2020-08-18 01:31:29
 */
public class CacheDemo {

    private UserService userService;

    private UserCacheLoadService userCacheLoadService;

    public static void main(String[] args) throws InterruptedException {

        CacheDemo simpleCache = new CacheDemo();
        simpleCache.userService = new UserService();
        simpleCache.userCacheLoadService = new UserCacheLoadService();

        // 查询 db 不带缓存
        long startTime = System.currentTimeMillis();
        simpleCache.queryWithoutCache();
        long withoutCacheTime = System.currentTimeMillis();
        System.out.println("without cache duration: " + (withoutCacheTime - startTime) + " ms, " +
                "query db count: " + simpleCache.userService.getQueryDBCounter());

        // 查询 db 带缓存
        simpleCache.userService.clearCache();
        simpleCache.queryWithCache();
        long withCacheTime = System.currentTimeMillis();
        System.out.println("with cache duration: " + (withCacheTime - withoutCacheTime) + " ms, " +
                "query db count: " + simpleCache.userService.getQueryDBCounter());

        // 并行查询 db 带缓存，多个线程会从 DB 中查询相同的数据
        simpleCache.userService.clearCache();
        simpleCache.concurrentQueryWithCache(3);
        long concurrentTime = System.currentTimeMillis();
        System.out.println("with cache duration: " + (concurrentTime - withCacheTime) + " ms, " +
                "query db count: " + simpleCache.userService.getQueryDBCounter());

        // 并行查询 db 带缓存，保证相同的数据只会从 DB 查询一次
        simpleCache.userService.clearCache();
        simpleCache.concurrentQueryWithCacheLoad(3);
        long cacheLoadTime = System.currentTimeMillis();
        System.out.println("with cache duration: " + (cacheLoadTime - concurrentTime) + " ms, " +
                "query db count: " + simpleCache.userCacheLoadService.getQueryDBCounter());
    }

    private void queryWithCache() {
        for (int i = 0; i < 5_000; i++) {
            long userId = i % 500;
            String userName = userService.queryWithCache(userId);
            String expectName = "name:" + userId;
            Preconditions.checkState(expectName.equals(userName));
        }
    }

    private void queryWithoutCache() {
        for (int i = 0; i < 5_000; i++) {
            long userId = i % 500;
            String userName = userService.queryWithoutCache(userId);
            String expectName = "name:" + userId;
            Preconditions.checkState(expectName.equals(userName));
        }
    }

    private void concurrentQueryWithCache(int numOfConcurrent) throws InterruptedException {

        ExecutorService es = new ThreadPoolExecutor(numOfConcurrent, numOfConcurrent,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch countDownLatch = new CountDownLatch(numOfConcurrent);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                queryWithCache();
                countDownLatch.countDown();
            }
        };
        for (int i = 0; i < numOfConcurrent; i++) {
            es.execute(task);
        }
        countDownLatch.await(20, TimeUnit.SECONDS);
        es.shutdown();
    }

    private void concurrentQueryWithCacheLoad(int numOfConcurrent) throws InterruptedException {

        ExecutorService es = new ThreadPoolExecutor(numOfConcurrent, numOfConcurrent,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch countDownLatch = new CountDownLatch(numOfConcurrent);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5_000; i++) {
                    long userId = i % 500;
                    // 使用 CacheLoad 查询
                    String userName = userCacheLoadService.queryWithCache(userId);
                    String expectName = "name:" + userId;
                    Preconditions.checkState(expectName.equals(userName));
                }
                countDownLatch.countDown();
            }
        };
        for (int i = 0; i < numOfConcurrent; i++) {
            es.execute(task);
        }
        countDownLatch.await(20, TimeUnit.SECONDS);
        es.shutdown();
    }
}
