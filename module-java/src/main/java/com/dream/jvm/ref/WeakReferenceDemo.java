package com.dream.jvm.ref;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author fanrui
 * 弱引用 demo
 */
public class WeakReferenceDemo {


    public static void main(String[] args) {
        Object o1 = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(o1);
        System.out.println(o1);
        System.out.println(weakReference.get());
        System.out.println(weakReference);

        o1 = null;
        System.gc();

        System.out.println(o1);
        System.out.println(weakReference.get());
        System.out.println(weakReference);
    }

}
