package com.dream.sort;

/**
 * 成对插入排序
 * @author fanrui
 * @time 2019-04-09 15:19:32
 * @desc 优化在于每次插入两个元素到已排序数据中，较少了数组的移动次数
 */
public class PairInsertSort implements ISort {


	public void sort(int[] array) {

		if (array.length <= 1) {
			return;
		}

		pairInsertSort(array,0, array.length-1);
	}

	private static void pairInsertSort(int[] a, int left, int right) {
		int length = right - left + 1;

        /*
         * Skip the longest ascending sequence.
         */
        do {
            if (left >= right) {
                return;
            }
        } while (a[++left] >= a[left - 1]);

        /*
         * Every element from adjoining part plays the role
         * of sentinel, therefore this allows us to avoid the
         * left range check on each iteration. Moreover, we use
         * the more optimized algorithm, so called pair insertion
         * sort, which is faster (in the context of Quicksort)
         * than traditional implementation of insertion sort.
         */
        for (int k = left; ++left <= right; k = ++left) {
            // a1 表示未排序部分的第一个数
            // a2 表示未排序部分的第二个数（for循环第一轮 left=k）
            int a1 = a[k], a2 = a[left];


            // 保证 a1 >= a2
            if (a1 < a2) {
                // a1 < a2 ,则交换
                a2 = a1; a1 = a[left];
            }

            // 已排序序列中，比 a1 大的数，往右移动2个位置，直到找到a1的位置
            while (k >0 && a1 < a[k-1]) {
                k--;
                a[k + 2] = a[k];
            }
            if ( k == 0 ){
                a[1] = a1;
                a[0] = a2;
                continue;
            } else {
                a[k + 1] = a1;
            }

            // a1之前找到a2的位置
            while (k >0 && a2 < a[k-1]) {
                k--;
                a[k + 1] = a[k];
            }
            a[k] = a2;
        }
        int last = a[right];

        while (last < a[--right]) {
            a[right + 1] = a[right];
        }
        a[right + 1] = last;

	}

}
