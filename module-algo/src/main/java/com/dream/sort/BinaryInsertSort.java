package com.dream.sort;

/**
 * 优化版的插入排序实现
 * @author fanrui
 * @time 2019-04-09 15:19:32
 * @desc 优化在于 通过二分查找，找出需要插入的位置，大量减少了比较次数，数组长度较长时，此优化点比较明显
 */
public class BinaryInsertSort implements ISort {


	public void sort(int[] array) {

		if (array.length <= 1) {
			return;
		}

		for (int i = 1; i < array.length; ++i) {
			int value = array[i];
			int index = bsearch(array,i,array[i]);

            if (i - index >= 0) {
                System.arraycopy(array, index, array, index + 1, i - index);
            }
            array[index] = value;
		}

	}

	// 查找第一个大于给定值的元素
	private static int bsearch(int[] a, int n, int value) {
		int low = 0;
		int high = n - 1;
		while (low <= high) {
			int mid =  low + ((high - low) >> 1);
			if (a[mid] > value) {
				if ((mid == 0) || (a[mid - 1] <= value)) {
					return mid;
				} else {
					high = mid - 1;
				}
			} else {
				low = mid + 1;
			}
		}
		return high+1;
	}

}
