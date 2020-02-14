package com.dream.design.pattern.create.singleton;

/**
 * @author fanrui
 * 懒汉式 实现单例模式
 */
public class LazyLoadSingleton {

    private static LazyLoadSingleton singleton;

    private LazyLoadSingleton(){
    }


    /**
     * 注意，getInstance 方法 一定要用 synchronized 修饰，
     * 否则不安全，可能导致 new 出多个对象
      */
    public static synchronized LazyLoadSingleton getInstance(){
        if(singleton == null){
            singleton = new LazyLoadSingleton();
        }
        return singleton;
    }
}
