package com.dream.juc.lock.sale.ticket;

/**
 * @author fanrui
 * @time 2019-12-05 00:02:42
 */
public class SynchronizedTicket implements Ticket {
    private int number = 30;

    @Override
    public synchronized void sale() {
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() +
                    "卖出" + (number--) + "\t 还剩" + number);
        }
    }

}
