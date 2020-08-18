package com.dream.guava.cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fanrui03
 * 模拟数据库里查询用户的信息
 */
public class UserDao {

    private AtomicLong counter;

    public UserDao() {
        this.counter = new AtomicLong();
    }

    public long getCounter() {
        return counter.get();
    }

    public void clearCounter() {
        counter.set(0);
    }

    public String queryUserNameById(long userId) {
        sleep();
        counter.incrementAndGet();
        return "name:" + userId;
    }

    private void sleep() {
        sleepMs(10);
    }

    private void sleepMs(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
