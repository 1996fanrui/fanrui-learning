package com.dream.juc.sync.base;


import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.StampedLock;

/**
 * @author fanrui
 * @desc 彻底搞懂 synchronized 用法
 */
public class SynchronizedTest {
    final static StampedLock lock = new StampedLock();
    public static void main(String[] args) throws InterruptedException {
        Person person = new Person();
        person.play();
//        new Thread(person::play,"A").start();
        Thread.sleep(100);
        new Thread(() -> {person.play();},"B").start();
    }
}
