package com.dream.juc.sync.base;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author fanrui
 * @time 2019-12-08 23:42:13
 */
public class Person {
//    public static synchronized void eat(){
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("eat");
//    }

    public void eat(){
        synchronized (this){
            System.out.println("play");
        }
    }

    public static synchronized void play(){
        System.out.println("play");
    }
}
