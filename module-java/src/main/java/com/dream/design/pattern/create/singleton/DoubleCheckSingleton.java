package com.dream.design.pattern.create.singleton;

/**
 * @author fanrui
 * 双重检查锁 实现单例模式
 */
public class DoubleCheckSingleton {

    // 必须 volatile 修饰
    private static volatile DoubleCheckSingleton singleton ;

    private DoubleCheckSingleton(){
    }


    // 必须 两次 check
    public static DoubleCheckSingleton getInstance(){
        if(singleton == null){
            synchronized (DoubleCheckSingleton.class){
                if(singleton == null){
                    singleton = new DoubleCheckSingleton();
                }
            }
        }
        return singleton;
    }
}
