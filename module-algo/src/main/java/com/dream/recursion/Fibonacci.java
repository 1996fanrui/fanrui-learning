package com.dream.recursion;

public class Fibonacci {

    public static void main(String[] args){
        int n = 45;
        long[] cache = new long[n+1];
        System.out.println(FibonacciRecursively(45,cache));
        System.out.println(Fibonacci(45));
    }

    /**
     * 使用递推的方式计算 Fibonacci
     * @param n
     * @return
     */
    private static long Fibonacci(int n) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        }
        long[] f = new long[n+1];
        f[0] = 0;
        f[1] = 1;
        for ( int i = 2; i <= n; i++){
            f[i] = f[i-1] + f[i-2];
        }
        return f[n];
    }

    /**
     * 优化递推的空间复杂度
     * @param n
     * @return
     */
    private static long Fibonacci2(int n) {
        if(n <= 1){
            return n;
        }
        int pre2 = 0;
        int pre = 1;
        int result =1;
        for(int i = 2; i <= n; i++){
            result = (pre + pre2)%1000000007;
            pre2 = pre;
            pre = result;
        }
        return result;
    }



    /**
     * 使用递归 加 记忆的方式计算 Fibonacci
     * 这里的记忆防止重复计算
     * @param n
     * @return
     */
    public static long FibonacciRecursively(int n,long[] cache) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
            // 缓存=0表示 cache[n] 还未计算，所以进行计算，并保存结果到缓存中
        } else if (cache[n] == 0) {
            cache[n] = FibonacciRecursively(n - 1,cache) + FibonacciRecursively(n - 2,cache);
        }
        return cache[n];
    }



    /**
     * 单纯使用递归的方式计算 Fibonacci
     * @param n
     * @return
     */
    public static long FibonacciRecursively(int n) {
        if (n <= 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return FibonacciRecursively(n - 1) + FibonacciRecursively(n - 2);
        }
    }

}
