package com.dream.serach;

/**
 * @author fanrui
 * @time 2020-03-10 23:21:49
 * 各种形式的二分查找：
 * 随便查找一个值等于给定值的元素
 * 查找第一个值等于给定值的元素
 * 查找最后一个值等于给定值的元素
 * 查找第一个大于等于给定值的元素
 * 查找最后一个小于等于给定值的元素
 *
 * 相关题目：
 * 不含重复元素，普通二分
 *      LeetCode 704：https://leetcode-cn.com/problems/binary-search/
 * 含重复元素，找等于给定值的第一个和最后一个元素的 index：
 *      LeetCode 34：https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 * 含重复元素，找出给定值在数组中出现的次数
 *      剑指 Offer 53-I：https://leetcode-cn.com/problems/zai-pai-xu-shu-zu-zhong-cha-zhao-shu-zi-lcof/
 *
 */
public class BinarySearch {

    // 随便查找一个值等于给定值的元素，找到并返回即可
    public static int searchData(int[] a, int value) {
        if(a == null){
            return -1;
        }

        int low = 0;
        int high = a.length - 1;
        while (low <= high) {
            int mid =  low + ((high - low) >> 1);
            if (a[mid] > value) {
                high = mid - 1;
            } else if (a[mid] < value) {
                low = mid + 1;
            } else {
               return mid;
            }
        }
        return -1;

    }


    // 查找第一个值等于给定值的元素
    // 找到值为 value 的元素后，还要判断前一个 元素是否小于 value，
    // 或者 当前就是全局的第一个元素了，然后返回
    public static int searchFirstData(int[] a, int value) {
        if(a == null){
            return -1;
        }

        int low = 0;
        int high = a.length - 1;
        while (low <= high) {
            int mid =  low + ((high - low) >> 1);
            if (a[mid] > value) {
                high = mid - 1;
            } else if (a[mid] < value) {
                low = mid + 1;
            } else {
                if(mid == 0 || a[mid-1] < value){
                    return mid;
                }
                high = mid - 1;
            }
        }
        return -1;

    }


    // 查找最后一个值等于给定值的元素
    // 找到值为 value 的元素后，还要判断后一个 元素是否大于 value，
    // 或者 当前就是全局的最后一个元素了，然后返回
    public static int searchLastData(int[] a, int value) {
        if(a == null){
            return -1;
        }

        int low = 0;
        int high = a.length - 1;
        while (low <= high) {
            int mid =  low + ((high - low) >> 1);
            if (a[mid] > value) {
                high = mid - 1;
            } else if (a[mid] < value) {
                low = mid + 1;
            } else {
                if(mid == a.length-1 || a[mid+1] > value){
                    return mid;
                }
                low = mid + 1;
            }
        }
        return -1;

    }


    // 查找第一个大于等于给定值的元素
    // 找到大于等于 value 的元素时，还要判断前一个 元素是否小于 value，
    // 或者 当前就是全局的第一个元素了，然后返回
    public static int searchFirstGeData(int[] a, int value) {
        if(a == null){
            return -1;
        }

        int low = 0;
        int high = a.length - 1;
        while (low <= high) {
            int mid =  low + ((high - low) >> 1);
            if (a[mid] >= value) {
                if(mid == 0 || a[mid-1] < value){
                    return mid;
                }
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1;
    }


    // 查找最后一个小于等于给定值的元素
    // 找到小于等于 value 的元素后，还要判断后一个 元素是否大于 value，
    // 或者 当前就是全局的最后一个元素了，然后返回
    public static int searchLastLeData(int[] a, int value) {
        if(a == null){
            return -1;
        }

        int low = 0;
        int high = a.length - 1;
        while (low <= high) {
            int mid =  low + ((high - low) >> 1);
            if (a[mid] > value) {
                high = mid - 1;
            } else {
                if(mid == a.length-1 || a[mid+1] > value){
                    return mid;
                }
                low = mid + 1;
            }
        }
        return -1;
    }



    // LeetCode 35. 搜索插入位置
    public int searchInsert(int[] nums, int target) {
        if(nums == null || nums.length == 0){
            return 0;
        }
        int L = 0;
        int R = nums.length - 1;
        // 找比 target 小的数里最大的数
        while(L <= R){
            int mid = L + ((R-L)>>1);
            // 大于等于 target，往左找
            if(nums[mid] >= target){
                R = mid - 1;
            } else {
                // 小于 target，往右找
                L = mid + 1;
                // mid 已经是最右位置，或者 mid 下一个元素大于等于 target，找到了结果
                if(mid == nums.length - 1 || nums[mid+1] >= target){
                    return mid +1;
                }
            }
        }
        return R < 0 ? 0 : R;
    }


}
