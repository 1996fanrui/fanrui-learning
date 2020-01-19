package com.dream.classloader;

/**
 * @author fanrui
 *
 * ObjectLoaderSequence 的静态代码块
 * ObjectLoaderSequence 的 main 方法
 * ----------------------------
 * ObjectLoaderSequence 的构造块
 * ObjectLoaderSequence 的无参构造器
 * ObjectLoaderSequence 的构造块
 * ObjectLoaderSequence 的无参构造器
 */
public class ObjectLoaderSequence {

    {
        System.out.println("ObjectLoaderSequence 的构造块");
    }


    static {
        System.out.println("ObjectLoaderSequence 的静态代码块");
    }

    public ObjectLoaderSequence(){
        System.out.println("ObjectLoaderSequence 的无参构造器");
    }

    public static void main(String[] args) {
        System.out.println("ObjectLoaderSequence 的 main 方法\n----------------------------");
        new ObjectLoaderSequence();
        new ObjectLoaderSequence();
    }
}
