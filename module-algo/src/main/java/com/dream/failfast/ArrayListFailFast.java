package com.dream.failfast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author fanrui
 * @time 2019-04-27 21:58:59
 */
public class ArrayListFailFast {

    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("three");
//        list.add("four");

        for ( String s : list ){
            if ( "two".equals(s) ){
                list.remove(s);
            }
            // 这里只会打印 one  two ，不会打印 three
            //      这里 remove two后，list的size从3变成2，在hasNext中发现 cursor=size=2，所以停止遍历，所以不会打印 three
            // 若 list中 加入了 four，则会抛异常
            //      这里 remove two后，list的size从4变成3，在hasNext中发现 cursor=2 != size=3 ,所以 继续遍历，
            //      执行next() ，但是next内发现 modCount != expectedModCount ，所以抛出异常
            System.out.println(s);
        }

        System.out.println(list);

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()){
            synchronized (iterator){
                String item = iterator.next();
                if ( "two".equals(item) ){
                    iterator.remove();
                }
            }
        }

        System.out.println(list);
    }

}
