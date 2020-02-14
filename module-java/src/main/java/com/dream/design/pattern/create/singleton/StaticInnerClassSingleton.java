package com.dream.design.pattern.create.singleton;

/**
 * @author fanrui
 * 静态内部类 实现单例模式
 */
public class StaticInnerClassSingleton {


    private StaticInnerClassSingleton(){
    }

    private static class SingletonInstance {
        private static final StaticInnerClassSingleton singleton
                = new StaticInnerClassSingleton();
    }

    public static StaticInnerClassSingleton getInstance(){
        return SingletonInstance.singleton;
    }

}
