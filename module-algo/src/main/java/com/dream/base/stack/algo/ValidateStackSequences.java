package com.dream.base.stack.algo;

/**
 * @author fanrui
 * 验证栈序列
 * 给定压栈顺序，判断出栈顺序是否可以实现
 * 给定 pushed 和 popped 两个序列，每个序列中的 值都不重复，
 * 只有当它们可能是在最初空栈上进行的推入 push 和弹出 pop 操作序列的结果时，返回 true；否则，返回 false 。
 * LeetCode 946：https://leetcode-cn.com/problems/validate-stack-sequences/
 * 剑指 31：https://leetcode-cn.com/problems/zhan-de-ya-ru-dan-chu-xu-lie-lcof/
 */
public class ValidateStackSequences {

    public boolean validateStackSequences(int[] pushed, int[] popped) {
        if (pushed == null || popped == null
                || pushed.length != popped.length) {
            return false;
        }

        int popIndex = 0;
        int size = 0;
        int[] stack = new int[pushed.length];

        // 每次往栈中压入一个数据
        for (int pushIndex = 0; pushIndex < pushed.length; ) {
            stack[size++] = pushed[pushIndex++];
            // while 循环，popped 数组与栈顶元素匹配。
            // 只要能匹配，则元素出栈，popped 指针右移，
            // 直到不能匹配成功则 继续下一轮的压栈。
            while (size > 0 && stack[size - 1] == popped[popIndex]) {
                popIndex++;
                size--;
            }
        }
        // popIndex 指向了最后，表示能完全匹配
        return popIndex == popped.length;
    }

}
