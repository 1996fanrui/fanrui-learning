package com.dream.design.pattern.create.singleton;

/**
 * @author fanrui
 * 饿汉式 实现单例模式
 */
public class HungrySingleton {

    private static HungrySingleton singleton = new HungrySingleton();

//    // 饿汉式 变种，使用静态代码块初始化
//    private static HungrySingleton singleton;
//    static {
//        singleton = new HungrySingleton();
//    }

    private HungrySingleton(){
    }


    public static HungrySingleton getInstance(){
        return singleton;
    }

}
