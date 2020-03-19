package com.dream.base.array;

/**
 * @author fanrui
 * @time 2020-03-20 00:15:51
 * 荷兰国旗问题
 * 小于 num 的放左边， num 放中间，大于 num 的放右边
 */
public class NetherlandsFlag {

    public static int[] partition(int[] arr, int l, int r, int num) {
        int less = l - 1;
        int more = r + 1;
        while (l < more) {
            if (arr[l] < num) {
                swap(arr, ++less, l++);
            } else if (arr[l] > num) {
                swap(arr, --more, l);
            } else {
                l++;
            }
        }
        return new int[]{less + 1, more - 1};
    }

    // for test
    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
