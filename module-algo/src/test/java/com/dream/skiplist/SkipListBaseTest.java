package com.dream.skiplist;

import org.junit.Test;

/**
 * 跳表 测试
 * @author fanrui
 * @time 2019-03-24 18:35:31
 * @see SkipList
 */
public class SkipListBaseTest {

    SkipList skipList = new SkipList();


    @Test
    public void testInsert(){
        skipList.insert(1);
        skipList.insert(2);
        skipList.insert(5);
        skipList.insert(3);
        skipList.insert(4);

        skipList.printAll();

        skipList.delete(3);

        skipList.printAll();



        SkipList.Node node = skipList.find(3);
        if ( node != null ){
            System.out.println(node.data);
            for (SkipList.Node nodeForward:node.forwards){
                System.out.println(nodeForward);
            }
            System.out.println(node.maxLevel);
        }
    }

}
