package com.dream.base.array;

/**
 * @author fanrui
 * @time 2020-03-20 00:15:51
 * 荷兰国旗问题
 * 小于 num 的放左边， num 放中间，大于 num 的放右边
 */
public class NetherlandsFlag {

    /**
     * less : All data to the left of less (with less) are less than num.
     * l : the current data (all data are num between less and l (without less and l))
     * more : All data to the right of more (with more) are greater than num.
     */
    public static int[] partition(int[] arr, int l, int r, int num) {
        int less = l - 1;
        int more = r + 1;
        while (l < more) {
            if (arr[l] < num) {
                //   num = 2
                //  less           l                more
                //   1   2  2  2  -1   x   x   x     5
                //     less            l             more
                //   1  -1  2  2   2   x   x   x     5
                swap(arr, ++less, l++);
            } else if (arr[l] > num) {
                //   num = 2
                //  less           l                more
                //   1   2  2  2   4   x   x   x     5
                //       less      l          more
                //   1  -1  2  2   x   x   x   4     5
                swap(arr, --more, l);
            } else {
                //   num = 2
                //  less           l                more
                //   1   2  2  2   2   x   x   x     5
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
