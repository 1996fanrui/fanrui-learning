package com.dream.jvm.ref;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * @author fanrui
 * WeakHashMap demo
 */
public class WeakHashMapDemo {


    public static void main(String[] args) {
        WeakHashMap<Integer, String> weakHashMap = new WeakHashMap<>();
        Integer key = new Integer(1);
        String value = "WeakHashMap";

        weakHashMap.put(key, value);
        System.out.println(weakHashMap);

        key = null;
        System.out.println(weakHashMap);

        System.gc();
        System.out.println(weakHashMap);
    }

}
