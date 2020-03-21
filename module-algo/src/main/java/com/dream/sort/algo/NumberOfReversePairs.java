package com.dream.sort.algo;

/**
 * @author fanrui
 * @time 2020-03-22 00:14:04
 * 逆序对-归并思想
 * 剑指 Offer 51： https://leetcode-cn.com/problems/shu-zu-zhong-de-ni-xu-dui-lcof/
 */
public class NumberOfReversePairs {

    int res;

    public int reversePairs(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return 0;
        }
        res = 0;
        mergeSort(nums, 0, nums.length - 1);
        return res;
    }


    private void mergeSort(int[] nums, int L, int R) {
        if (L == R) {
            return;
        }
        int mid = L + ((R - L) >> 1);
        // 归并排序左半个数组 + 右半个数组 + merge 左右数组
        mergeSort(nums, L, mid);
        mergeSort(nums, mid + 1, R);
        // 归并优化，如果前数组的最后元素 小于等于 后数组的第一个元素，
        // 表示全局是有序的，return 即可
        if(nums[mid]<= nums[mid+1]){
            return;
        }
        merge(nums, L, mid, R);
    }

    private void merge(int[] nums, int L, int mid, int R) {
        int[] help = new int[R - L + 1];
        int helpIndex = 0;
        int l = L;
        int r = mid + 1;

        while (l <= mid && r <= R) {
            if (nums[l] <= nums[r]) {
                // 重点，左边的数组元素要放到 help 数组了，要检测 右边数组有几个已经放到 help 数组，
                // 也就是逆序对个数
                res += r - (mid + 1);

                help[helpIndex++] = nums[l++];
            } else {
                help[helpIndex++] = nums[r++];
            }
        }

        while (l <= mid) {
            // 重点，左边的数组元素要放到 help 数组了，要检测 右边数组有几个已经放到 help 数组，
            // 也就是逆序对个数
            res += R - mid;

            help[helpIndex++] = nums[l++];
        }

        while (r <= R) {
            help[helpIndex++] = nums[r++];
        }

        for (int i = 0; i < help.length; i++) {
            nums[L + i] = help[i];
        }
    }


}
