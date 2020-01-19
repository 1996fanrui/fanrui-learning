package com.dream.jvm.ref;

import java.lang.ref.SoftReference;

/**
 * @author fanrui
 * 软引用 demo，常用于缓存
 * 注：SoftReference 对象本身也是强引用，只不过 SoftReference 内部包装的对象是软引用，可以被回收。
 */
public class SoftReferenceDemo {


    /**
     * 内存够用，则保留，不够用就回收
     */
    public static void softRefMemoryEnough(){
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        o1 = null;
        System.gc();

        System.out.println(o1);
        System.out.println(softReference.get());
    }


    /**
     * 内存不够用，则软引用被回收
     * -Xms5m -Xmx5m -XX:PrintGCDetails
     */
    public static void softRefMemoryNotEnough(){
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());
        System.out.println(softReference);

        o1 = null;
        System.gc();

        try{
            byte[] bytes = new byte[30 * 1024 * 1024];
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            System.out.println(o1);
            System.out.println(softReference.get());
            System.out.println(softReference);
        }
    }

    public static void main(String[] args) {
//        softRefMemoryEnough();


        softRefMemoryNotEnough();
    }

}
