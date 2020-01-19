package com.dream.jvm.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.PhantomReference;

/**
 * @author fanrui
 * ReferenceQueue demo
 *
 * SoftReference、WeakReference、PhantomReference 都可以与 ReferenceQueue 搭配使用
 */
public class ReferenceQueueDemo {


    public static void main(String[] args) {
        Object o1 = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(o1, referenceQueue);
        System.out.println(o1);
        System.out.println(phantomReference.get());
        System.out.println(phantomReference);
        Reference<?> poll = referenceQueue.poll();
        System.out.println(poll);
        if(poll!= null){
            System.out.println(poll.get());
        }

        o1 = null;
        System.gc();
        System.out.println("===================================");

        System.out.println(o1);
        System.out.println(phantomReference.get());
        System.out.println(phantomReference);
        poll = referenceQueue.poll();
        System.out.println(poll);
        if(poll!= null){
            System.out.println(poll.get());
        }
    }

}
