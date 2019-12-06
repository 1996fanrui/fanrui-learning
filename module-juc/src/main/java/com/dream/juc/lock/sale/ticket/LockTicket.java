package com.dream.juc.lock.sale.ticket;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fanrui
 * @time 2019-12-05 00:02:21
 */
public class LockTicket implements Ticket {
    private int number=30;

    private Lock lock = new ReentrantLock();

    @Override
    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() +
                        "卖出" + (number--) + "\t 还剩" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
