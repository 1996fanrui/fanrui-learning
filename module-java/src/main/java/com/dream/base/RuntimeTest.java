package com.dream.base;

import java.util.Random;

/**
 * @author fanrui
 * -Xms1024m -Xmx1024m -XX:+PrintGCDetails
 */
public class RuntimeTest {

    public static void main(String[] args) {

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("availableProcessors = " + availableProcessors);

        //返回 Java 虚拟机试图使用的最大内存量。
        long maxMemory = Runtime.getRuntime().maxMemory() ;
        //返回 Java 虚拟机中的内存总量。
        long totalMemory = Runtime.getRuntime().totalMemory() ;
        System.out.println("-Xms MAX_MEMORY = " + maxMemory + "（字节）、" + (maxMemory / (double)1024 / 1024) + "MB");
        System.out.println("-Xmx TOTAL_MEMORY = " + totalMemory + "（字节）、" + (totalMemory / (double)1024 / 1024) + "MB");

        String str = "123456" ;
        while(true) {
            str += str + new Random().nextInt(88888888) + new Random().nextInt(999999999) ;
        }

    }
}
