package com.dream.juc.lock.sale;

import com.dream.juc.lock.sale.ticket.LockTicket;
import com.dream.juc.lock.sale.ticket.Ticket;


/**
 *
 * @author fanrui
 * @desc 卖票程序售票
 */
public class SaleTicket {
    public static void main(String[] args) {//main所有程序

//        Ticket ticket = new SynchronizedTicket();
        Ticket ticket = new LockTicket();

        new Thread(() -> {
            for (int i = 1; i < 40; i++) {
                ticket.sale();
            }
        }, "AA").start();

        new Thread(() ->{
            for (int i = 1; i < 40; i++) {
                ticket.sale();
            }
        }, "BB").start();

        new Thread(() -> {
            for (int i = 1; i < 40; i++) {
                ticket.sale();
            }
        }, "CC").start();

    }
}
