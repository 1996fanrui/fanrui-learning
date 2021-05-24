package com.dream.base;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @date 2021/5/20 19:33
 */
public class StackTraceTest {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        long threadId = thread.getId();
        printThreadStack(threadId);
    }

    private static void printThreadStack(long threadId) {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo threadInfo = threadMxBean.getThreadInfo(threadId, 100);
        System.out.println("ThreadState : " + threadInfo.getThreadState());
        System.out.println("ThreadState StackTrace : ");
        for (StackTraceElement stackTrace : threadInfo.getStackTrace()) {
            System.out.println(stackTrace);
        }
    }
}
