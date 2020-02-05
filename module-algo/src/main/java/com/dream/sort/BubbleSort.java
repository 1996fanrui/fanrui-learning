package com.dream.sort;

/**
 * 冒泡排序<br>
 * 时间复杂度: 平均情况与最差情况都是O(n^2)
 * 空间复杂度: O(1)
 * @author fanrui
 * @time  2019-03-21 15:07:06
 * @see ISort
 * @see SortTest
 */
public class BubbleSort implements ISort {

	public void sort(int[] array) {
		if (array.length <= 1) {
			return;
		}

		for (int i = 0; i < array.length; ++i) {
			// 提前退出冒泡循环的标志位
			boolean flag = false;
			for (int j = 0; j < array.length - i - 1; ++j) {
				// 交换
				if ( array[j] > array[j+1] ) {
					int tmp = array[j];
					array[j] = array[j+1];
					array[j+1] = tmp;
					// 表示有数据交换
					flag = true;
				}
			}
			// 没有数据交换，提前退出
			if (!flag) {
				break;
			}
		}
	}

}
