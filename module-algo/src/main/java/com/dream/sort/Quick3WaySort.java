package com.dream.sort;

/**
 * 三路快速排序
 * 时间复杂度: 平均情况是O(nlog(n)),最差情况是O(n^2)
 * 空间复杂度: O(1)
 * @author fanrui
 * @time 2019-04-10 19:55:43
 * @see ISort
 * @see SortTest
 */
public class Quick3WaySort implements ISort {

	public void sort(int[] array) {
		quick3WaySort(array, 0, array.length - 1);
	}

	/**
	 * 从 p 到 right 排序数组array
	 * @param array
	 * @param left
	 * @param right
	 */
	private void quick3WaySort(int[] array, int left, int right){
		if (left >= right) {
			return;
		}

		// 选第一个做为分区点
		int pivot = array[left];
		// lt 表示已经交换完的数据中 比 pivot小的数的下标最大值
		int lt = left;
		// gt 表示已经交换完的数据中 比 pivot大的数的下标最小值
		int gt = right+1;
		// i 表示 当前处理的数，从左往右处理
		int i = left+1;
		while( i < gt ) {
			if ( array[i] < pivot) {
			    swap(array,i,lt+1);
				lt++;
				i++;
			} else if( array[i] > pivot ) {
				swap(array,i,gt-1);
				gt--;
			} else {
				i++;
			}
		}


		swap(array,left,lt);

		quick3WaySort(array, left, lt-1);
		quick3WaySort(array, gt, right);
	}

	private void swap(int[] array,int i,int j){
	    int temp = array[i];
	    array[i] = array[j];
	    array[j] = temp;
    }

}
