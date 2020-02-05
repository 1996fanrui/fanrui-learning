package com.dream.sort;

import java.util.Arrays;

/**
 * @author fanrui
 * @time 2019-04-10 17:06:06
 * @desc 使用Java的 Arrays.sort(T[]),底层使用 TimSort
 */
public class TimSort implements ObjectSort {


	@Override
    public void sort(Object[] array) {

		if (array.length <= 1) {
			return;
		}

		Arrays.sort(array);
	}


}
