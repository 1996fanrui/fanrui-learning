package com.dream.juc.ratelimiter;

public abstract class Limiter {

    //当前令牌桶中的令牌数量
    long storedPermits = 0;
    //令牌桶的容量
    long maxPermits = 100;
    //下一令牌产生时间
    long next = System.nanoTime();
    //发放令牌间隔：纳秒
    long interval = 1000_000_000;


    abstract void resync(long now);
    abstract long reserve(long now);
    abstract void acquire();
}
