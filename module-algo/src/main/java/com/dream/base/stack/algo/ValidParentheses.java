package com.dream.base.stack.algo;

import java.util.HashMap;
import java.util.Stack;

/**
 * @author fanrui
 * 有效的括号
 * LeetCode 20：https://leetcode-cn.com/problems/valid-parentheses/
 *
 */
public class ValidParentheses {

    private static HashMap<Character, Character> map = new HashMap<>();

    static {
        map.put(')','(');
        map.put(']','[');
        map.put('}','{');
    }

    public boolean isValid(String s) {

        if(s == null || s.length() == 0){
            return true;
        }

        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < chars.length; i++) {
            if(map.containsKey(chars[i])){
                if(stack.isEmpty()){
                    return false;
                }
                Character pop = stack.pop();
                if(!pop.equals(map.get(chars[i]))){
                    return false;
                }
            } else {
                stack.push(chars[i]);
            }
        }
        return stack.isEmpty();
    }
}
