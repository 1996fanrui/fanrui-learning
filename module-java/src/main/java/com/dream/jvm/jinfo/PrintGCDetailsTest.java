package com.dream.jvm.jinfo;

import java.lang.management.ManagementFactory;

/**
 * @author fanrui
 *
 * JVM: -Xmx20m -Xms20m -Xmn2m
 *
 * jinfo -flag PrintGCDetails 36359
 *
 * jinfo -flag +PrintGC 36359
 * jinfo -flag +PrintGCDetails 36359
 * jinfo -flag +PrintGCTimeStamps 36359 // 打印程序启动多久了
 *
 */
public class PrintGCDetailsTest {

    public static void main(String[] args) {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        String pid = name.split("@")[0];
        System.out.println("Pid is:" + pid);

        while(true)
        {
            byte[] b = null;
            for (int i = 0; i < 10; i++){
                b = new byte[1 * 1024 * 1024];
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
