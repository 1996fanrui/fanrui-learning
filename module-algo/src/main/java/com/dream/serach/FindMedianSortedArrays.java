package com.dream.serach;

/**
 * 4. 寻找两个正序数组的中位数
 * https://leetcode.cn/problems/median-of-two-sorted-arrays/description/
 */
public class FindMedianSortedArrays {

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int m = nums2.length;
        //因为数组是从索引0开始的，因此我们在这里必须+1，即索引(k+1)的数，才是第k个数。
        int left = (n + m + 1) / 2;
        int right = (n + m + 2) / 2;
        //将偶数和奇数的情况合并，如果是奇数，会求两次同样的 k
        return (getKth(nums1, 0, n - 1, nums2, 0, m - 1, left) + getKth(nums1, 0, n - 1, nums2, 0, m - 1, right)) * 0.5;
    }

    private int getKth(int[] nums1, int start1, int end1, int[] nums2, int start2, int end2, int k) {
        // 因为索引和算数不同6-0=6，但是是有7个数的，因为end初始就是数组长度-1构成的。
        // 最后len代表当前数组(也可能是经过递归排除后的数组)，符合当前条件的元素的个数
        int len1 = end1 - start1 + 1;
        int len2 = end2 - start2 + 1;

        // 如果一个数组中没有了元素，那么即从剩余数组nums2的其实start2开始加k再-1.
        // 因为k代表个数，而不是索引，那么从nums2后再找k个数，那个就是start2 + k-1索引处就行了。因为还包含nums2[start2]也是一个数。因为它在上次迭代时并没有被排除
        if (len1 == 0) {
            return nums2[start2 + k - 1];
        }
        if (len2 == 0) {
            return nums1[start1 + k - 1];
        }

        // 如果k=1，表明最接近中位数了，即两个数组中start索引处，谁的值小，中位数就是谁(start索引之前表示经过迭代已经被排出的不合格的元素，即数组没被抛弃的逻辑上的范围是nums[start]--->nums[end])。
        if (k == 1) {
            return Math.min(nums1[start1], nums2[start2]);
        }

        //为了防止数组长度小于 k/2,每次比较都会从当前数组所生长度和k/2作比较，取其中的小的(如果取大的，数组就会越界)
        //然后素组如果len1小于k / 2，表示数组经过下一次遍历就会到末尾，然后后面就会在那个剩余的数组中寻找中位数
        int i = start1 + Math.min(len1, k / 2) - 1;
        int j = start2 + Math.min(len2, k / 2) - 1;

        //如果nums1[i] > nums2[j]，表示nums2数组中包含j索引，之前的元素，逻辑上全部淘汰，即下次从J+1开始。
        //而k则变为k - (j - start2 + 1)，即减去逻辑上排出的元素的个数(要加1，因为索引相减，相对于实际排除的时要少一个的)
        if (nums1[i] > nums2[j]) {
            return getKth(nums1, start1, end1, nums2, j + 1, end2, k - (j - start2 + 1));
        } else {
            return getKth(nums1, i + 1, end1, nums2, start2, end2, k - (i - start1 + 1));
        }
    }
}
