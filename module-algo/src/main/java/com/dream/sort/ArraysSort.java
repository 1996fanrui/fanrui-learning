package com.dream.sort;

import java.util.Arrays;

/**
 * @author fanrui
 * @time 2019-04-10 17:06:06
 * @desc 使用Java的Arrays.sort(int[] a),底层使用 DualPivotQuicksort
 */
public class ArraysSort implements ISort {


	@Override
    public void sort(int[] array) {

		if (array.length <= 1) {
			return;
		}

		Arrays.sort(array);
	}


}
