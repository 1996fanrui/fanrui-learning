package com.dream.base.algo;

import java.util.*;
import java.io.*;

/**
 * @author fanrui
 * 	公式字符串求值
 * 	牛客链接：https://www.nowcoder.com/practice/c590e97ee1f6462d871430feee055d25?tpId=101&tqId=33196&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 */
public class ExpressionCompute {

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String str = in.readLine();
        System.out.println(getValue(str));
    }

    public static int getValue(String str) {
        return calValue(str.toCharArray(), 0)[0];
    }

    private static int[] calValue(char[] str, int index) {
        LinkedList<String> queue = new LinkedList<>();

        int curVal = 0;
        while (index < str.length && str[index] != ')') {
            if(str[index] >= '0' && str[index] <= '9') {
                curVal = curVal * 10 + str[index++] - '0';
            } else if( str[index] == '(' ){
                int[] subRes = calValue(str, ++index);
                curVal = subRes[0];
                index = subRes[1];
            } else {    // + - * /
                addNum2Queue(queue, curVal);
                queue.addLast(String.valueOf(str[index++]));
                curVal = 0;
            }
        }

        addNum2Queue(queue, curVal);

        return new int[]{ computeNum(queue), ++index};
    }


    private static void addNum2Queue(LinkedList<String> queue, int num){
        if(!queue.isEmpty()){
            String last = queue.peekLast();
            if("*".equals(last) || "/".equals(last)){
                queue.pollLast();
                int s = Integer.parseInt(queue.pollLast());
                num = "*".equals(last) ? s * num : s / num;
            }
        }
        queue.addLast(Integer.toString(num));
    }


    private static int computeNum(LinkedList<String> queue){
        int res = 0;
        int add = 1;
        while (!queue.isEmpty()){
            String cur = queue.pollFirst();
            if("+".equals(cur) || "-".equals(cur)) {
                add = "+".equals(cur) ? 1 : -1;
                cur = queue.pollFirst();
            }
            res += add * Integer.parseInt(cur);
        }
        return res;
    }
}
