package com.dream.base.array;


/**
 * @author fanrui
 * @time 2020-03-20 00:26:18
 * 剑指 Offer 21：https://leetcode-cn.com/problems/diao-zheng-shu-zu-shun-xu-shi-qi-shu-wei-yu-ou-shu-qian-mian-lcof/
 * 奇数放左边，偶数放右边（01问题）
 * 思路：搞两个指针，L 从左往右， R 从右往左。
 *      L 发现了偶数则停，R 发现了奇数则停。
 *      若 L 小于 R，则交换
 */
public class ProblemOf01 {

    public int[] exchange(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return nums;
        }
        int L = 0;
        int R = nums.length - 1;
        while (L < R) {
            //  L 位置是奇数，则 L 往右。L 找到偶数则停
            while ((nums[L] & 1) == 1 && L < R) {
                L++;
            }

            //  R 位置是奇数，则 R 往左，R 找到奇数则停
            while ((nums[R] & 1) == 0 && L < R) {
                R--;
            }
            if (L < R) {
                int tmp = nums[L];
                nums[L] = nums[R];
                nums[R] = tmp;
            }
        }
        return nums;
    }
}
