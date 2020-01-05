package com.dream.juc.ratelimiter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author fanrui
 * @time  2020-01-05 18:02:58
 */
public class RateLimiterTest {
    
    Limiter limiter;
    
    @Before
    public void before(){
//        limiter = new MyRateLimiter();
        limiter = new GeekTimeLimiter();
    }


    /**
     * 每 1.9 秒请求一次，理论来讲，19 秒之后，桶中还有 9块令牌
     * @throws InterruptedException
     */
    @Test
    public void testAcquireNumber() throws InterruptedException {
        long start = System.currentTimeMillis();

        for(int i = 0; i < 10;i++){
            TimeUnit.MILLISECONDS.sleep(1900);
            limiter.acquire();
            System.out.println(System.currentTimeMillis() - start);
            System.out.println(limiter.storedPermits);
        }
        Assert.assertTrue("桶中剩余的令牌数错误", 9 == limiter.storedPermits);
    }


    /**
     * Resync 方法的功能仅仅是将 时间转换为令牌的操作，并更新下一次产生令牌的时间。不消耗令牌
     * 1900 毫秒转换一次，19秒后，应该有 19 块令牌
     * @throws InterruptedException
     */
    @Test
    public void testResyncNumber() throws InterruptedException {
        long start = System.currentTimeMillis();

        for(int i = 0; i < 10;i++){
            TimeUnit.MILLISECONDS.sleep(1900);
            limiter.resync(System.nanoTime());
            System.out.println(System.currentTimeMillis() - start);
            System.out.println(limiter.storedPermits);
        }
        Assert.assertTrue("桶中剩余的令牌数错误", 19 == limiter.storedPermits);
    }

}
