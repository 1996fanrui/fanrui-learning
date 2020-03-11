package com.dream.sum.subarray;

/**
 * @author fanrui
 * @time 2020-03-11 20:59:44
 * 数组arr，值可正、可负、可0，求累加 和小于等于aim的，最长子数组
 * 牛客链接：https://www.nowcoder.com/practice/3473e545d6924077a4f7cbc850408ade?tpId=101&tqId=33082&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 */
public class MaxLengthSubarrayOfSumLeAim {


    public static int maxLengthAwesome(int[] arr, int aim) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int[] sums = new int[arr.length];
        int[] ends = new int[arr.length];
        // 初始化 sums 和 ends 数组
        sums[arr.length - 1] = arr[arr.length - 1];
        ends[arr.length - 1] = arr.length - 1;
        for (int i = arr.length - 2; i >= 0; i--) {
            // 下一个 sum 值小于 0 ，则当前元素加下一个 sum
            if (sums[i + 1] < 0) {
                sums[i] = arr[i] + sums[i + 1];
                ends[i] = ends[i + 1];
            } else {
                //  下一个 sum 等于 0，则当前元素自己本身就是最小值
                sums[i] = arr[i];
                ends[i] = ends[i];
            }
        }

        // R 表示窗口的右边界
        int R = 0;
        // sum 表示当前窗口内的 累加和
        int sum = 0;
        // res 用来记录结果，窗口运行过程中的最大值
        int res = 0;
        for (int L = 0; L < arr.length; L++) {
            // 检测窗口是否能往右扩，如果可以，一直扩，并累加到 sum 中
            while (R < arr.length && sum + sums[R] <= aim) {
                sum += sums[R];
                R = ends[R] + 1;
            }
            // 窗口左边界右移， sum 值 - 左边界的值，如果 R 不大于 L 说明窗口大小为 0
            sum -= R > L ? arr[L] : 0;
            // res 永远保存窗口最大值
            res = Math.max(res, R - L);
            // 这一步就是当 R = L，不能向右扩充时，强制将 R 右移
            R = Math.max(R, L + 1);
        }
        return res;
    }
}
