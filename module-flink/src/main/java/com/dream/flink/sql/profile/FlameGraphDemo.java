package com.dream.flink.sql.profile;

import com.dream.flink.util.HashUtil;

/**
 * @author fanrui
 * @date 2021/5/28 20:10
 */
public class FlameGraphDemo {

    public static void main(String[] args) {
        while (true) {
            methodA();
        }
    }

    private static void methodA() {
        methodB();
        methodC();
    }

    private static void methodB() {
        md5(50);
    }

    private static void methodC() {
        md5(20);
        methodD();
        methodE();
    }

    private static void methodD() {
        md5(15);
    }

    private static void methodE() {
        md5(15);
    }

    private static void md5(int count) {
        String s = "Hello Flame Graph.";
        for (int i = 0; i < count; i++) {
            s = HashUtil.md5(s);
        }
    }

}
