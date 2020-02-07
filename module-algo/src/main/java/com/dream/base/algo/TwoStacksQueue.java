package com.dream.base.algo;

import java.util.Stack;

/**
 * @author fanrui
 * 如何仅用栈结构实现队列结构?
 */
public class TwoStacksQueue {

    private Stack<Integer> stackPush;
    private Stack<Integer> stackPop;

    public TwoStacksQueue() {
        stackPush = new Stack<Integer>();
        stackPop = new Stack<Integer>();
    }

    public void push(int pushInt) {
        stackPush.push(pushInt);
    }

    public int poll() {
        if (stackPop.empty() && stackPush.empty()) {
            throw new RuntimeException("Queue is empty!");
        }
        move();
        return stackPop.pop();
    }

    public int peek() {
        if (stackPop.empty() && stackPush.empty()) {
            throw new RuntimeException("Queue is empty!");
        }
        move();
        return stackPop.peek();
    }

    /**
     * 搬运数据必须要求 stackPop 是空，且搬运必须一次搬完
      */
    private void move() {
        if (stackPop.empty()) {
            while (!stackPush.empty()) {
                stackPop.push(stackPush.pop());
            }
        }
    }
}
