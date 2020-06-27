package com.dream.design.pattern.action.observer;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author fanrui03
 * @time 2020-06-27 23:20:19
 * 观察者模式
 * 注：观察者的方法如果参数为 long 类型，最后不会被调用，需要改为 Long
 */
public class UserController {

    private final EventBus eventBus;
    private static final int DEFAULT_EVENTBUS_THREAD_POOL_SIZE = 20;
    private final ExecutorService fixedThreadPool;


    public UserController() {
        fixedThreadPool = Executors.newFixedThreadPool(DEFAULT_EVENTBUS_THREAD_POOL_SIZE);
        // 同步阻塞模式
//         eventBus = new EventBus();

        // 异步非阻塞模式
        eventBus = new AsyncEventBus(fixedThreadPool);
    }

    public void setRegObservers(List<Object> observers) {
        for (Object observer : observers) {
            eventBus.register(observer);
        }
    }

    public Long login(long userId) {
        eventBus.post(userId);
        return userId;
    }

    // 登录 观察者
    private static class LoginObserver {

        @Subscribe
        public void login(Long userId) {
            System.out.println(userId + " 登录成功");
        }
    }

    // 签到观察者
    private static class CheckInObserver {

        @Subscribe
        public void checkIn(Long userId) {
            System.out.println(userId + " 签到成功");
        }
    }

    // 发红包观察者
    private static class RedPackageObserver {

        @Subscribe
        public void sendRedPackage(Long userId) {
            System.out.println(userId + " 发送红包成功");
        }
    }

    private void shutdown() {
        fixedThreadPool.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        UserController userController = new UserController();
        // 注册 Observers
        ArrayList<Object> observers = new ArrayList<>();
        observers.add(new LoginObserver());
        observers.add(new CheckInObserver());
        observers.add(new RedPackageObserver());
        userController.setRegObservers(observers);

        System.out.println("Register Observers 成功");


        userController.login(10);

        TimeUnit.SECONDS.sleep(1);
        userController.shutdown();
    }
}