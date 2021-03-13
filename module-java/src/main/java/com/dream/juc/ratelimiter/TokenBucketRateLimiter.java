package com.dream.juc.ratelimiter;

import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter {
    /**
     * 令牌桶的容量「限流器允许的最大突发流量」
     */
    private final long capacity;
    /**
     * 多少纳秒生成一个令牌
     */
    private final long generatedTime;
    /**
     * 最后一个令牌发放的时间
     */
    private long lastTokenTime;
    /**
     * 当前令牌数量
     */
    private float currentTokens;

    public TokenBucketRateLimiter(long generatedPerSeconds, int capacity) {
        this.generatedTime = TimeUnit.SECONDS.toNanos(1) / generatedPerSeconds;
        this.capacity = capacity;
        this.lastTokenTime = System.nanoTime();
    }

    /**
     * 尝试获取令牌
     *
     * @return true表示获取到令牌，放行；否则为限流
     */
    public synchronized boolean tryAcquire() {
        /**
         * 计算令牌当前数量
         * 请求时间在最后令牌是产生时间相差大于等于额1s（为啥时1s？因为生成令牌的最小时间单位时s），则
         * 1. 重新计算令牌桶中的令牌数
         * 2. 将最后一个令牌发放时间重置为当前时间
         */
        long now = System.nanoTime();
        if (now > lastTokenTime) {
            float newPermits = 1.0f * (now - lastTokenTime) / generatedTime;
            currentTokens = Math.min(currentTokens + newPermits, capacity);
            lastTokenTime = now;
        }
        if (currentTokens > 0) {
            currentTokens--;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(1000, 10);
        int count = 0;

        while (true) {
            boolean b = rateLimiter.tryAcquire();
            if (!b) {
                continue;
            }
            count++;
            if (count % 1000 == 0) {
                long timeMillis = System.currentTimeMillis();
                System.out.println("timeMillis : " + timeMillis + "  count :" + count);
            }
        }
    }
}
