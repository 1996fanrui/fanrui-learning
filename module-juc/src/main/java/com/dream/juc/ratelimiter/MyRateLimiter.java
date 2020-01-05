package com.dream.juc.ratelimiter;

import java.util.concurrent.TimeUnit;

import static java.lang.Long.min;
import static java.lang.Math.max;

/**
 * @author fanrui
 * @time 2020-01-05 17:40:32
 * 根据 GeekTimeLimiter 改编
 */
public class MyRateLimiter extends Limiter{


    /**
     * 重新计算令牌桶中的令牌数，并更新下一令牌发放时间
     * 该方法只是根据 next 将该发放的令牌发放一下
     * @param now 当前时间戳
     */
    @Override
    public void resync(long now) {
        // 请求时间在下一令牌产生时间之后，说明桶中应该要增加令牌
        // 1、 需要重新计算桶中的令牌数
        // 2、 将下一个令牌发放时间重置为当前时间
        if (now > next) {
            // 新产生的令牌数
            long newPermits = (now - next) / interval;
            // 新令牌增加到令牌桶
            storedPermits = min(maxPermits, storedPermits + newPermits);
            // 计算漏掉的时间
            long diff = (now - next) % interval;
            // 将下一个令牌发放时间重置为当前时间
            next = now - diff;
        }
        // 如果请求时间在下一令牌产生时间之后，说明桶中不需要增加令牌，所以不执行任何逻辑
    }

    /**
     *
     * @param now 当前时间戳
     * @return 能够获取令牌的时间
     */
    @Override
    public synchronized long reserve(long now) {
        resync(now);
        // 能够获取令牌的时间
        long at = next;

        // 若 桶中没有令牌，需要将 next 加 interval
        if(storedPermits == 0){
            this.next += interval;
        } else {
            // 若桶中有令牌，需要将 令牌数 -1
            this.storedPermits -= 1;
        }
        return at;
    }

    /**
     * 申请令牌
     */
    @Override
    public void acquire() {
        //申请令牌时的时间
        long now = System.nanoTime();
        // 预占令牌
        long at = reserve(now);
        if (now < at) {
            try {
                TimeUnit.NANOSECONDS.sleep(at-now);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
