package com.dream.sort;

import org.junit.Test;

import java.util.*;

public class JdkTest {


    @Test
    public void testArraysSort(){
//        Arrays.sort(new int[1]);
        List<Integer> list = new LinkedList<Integer>();
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(1);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        for(Object element :list){
            System.out.print(element+"  ");
        }
        System.out.println();

        Collections.sort(list);
        for(Object element :list){
            System.out.print(element+"  ");
        }


    }
}
