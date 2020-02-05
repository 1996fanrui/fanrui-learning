package com.dream.sort;

/**
 * 选择排序实现
 * @author fanrui
 * @time 2019-03-22 10:05:24
 * @desc  选择排序算法的实现思路有点类似插入排序，分已排序区间和未排序区间。
 * 但是选择排序每次会从未排序区间中找到最小的元素，将其放到已排序区间的末尾。
 */
public class SelectionSort implements ISort {

	public void sort(int[] array) {

		if (array.length <= 1) {
			return;
		}

		for (int i = 0; i < array.length - 1; ++i) {
			// 查找未排序序列中最小值的索引
			int minIndex = i;
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] < array[minIndex]) {
					minIndex = j;
				}
			}

			// 交换
			int tmp = array[i];
			array[i] = array[minIndex];
			array[minIndex] = tmp;
		}
	}

}
