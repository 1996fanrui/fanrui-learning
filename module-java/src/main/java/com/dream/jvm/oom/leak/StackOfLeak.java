package com.dream.jvm.oom.leak;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * @author fanrui
 * @time 2020-05-07 10:43:06
 * 设计的栈:可能会发生内存泄露
 * 引用自 《Effective Java》第三版 第7条：消除过期的对象引用
 */
public class StackOfLeak {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackOfLeak() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }


    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }


    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        // 这里虽然 --size 进行了缩容，但 Stack 的 elements 数组仍然指向对应的元素
        // 元素用完不会释放（元素扔可达）
        return elements[--size];
    }


    public Object popCorrect(){
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object res = elements[--size];
        // 这里切换引用关系，如果外部也没有引用指向 res 对象，则可以被回收
        elements[size] = null;
        return res;
    }


    private void ensureCapacity() {
        if(size < elements.length){
            return;
        }
        elements = Arrays.copyOf(elements, 2 * size + 1);
    }


}
