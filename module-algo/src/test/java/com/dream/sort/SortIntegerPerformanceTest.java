package com.dream.sort;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 排序测试
 * @author fanrui
 * @time 2019-03-21 15:50:13
 * @see ISort
 * @see BubbleSort
 * @see SelectionSort
 * @see MergeSort
 * @see QuickSort
 */
public class SortIntegerPerformanceTest {
    private int arrayCount = 10000;
    private int arrayLength = 500;
//    private int arrayCount = 500;
//    private int arrayLength = 10000;
    private Object[][] array;
    private ObjectSort sort;
    private long startTime;
    private long endTime;

    @Before
    public void before() {
        System.out.println( "before..." );
        array = new Integer[arrayCount][arrayLength];

        for (int i=0; i<arrayCount; i++ ){
            for ( int j = 0; j < arrayLength; j++ ) {
                array[i][j] = (int)(100 * Math.random());
            }
        }

        startTime = System.currentTimeMillis();
    }

    @After
    public void after(){
        for (int i=0; i<arrayCount; i++ ) {
            sort.sort(array[i]);
            validate(array[i]);
        }
        endTime = System.currentTimeMillis();
        System.out.println( (endTime - startTime) + "ms" );
    }


    @Test
    public void testQuickSort(){
        sort = new ObjectQuickSort();     // 数组长度变成 500，10000个数组 200ms（如果数组长度变成10000，500个数组，那么快排性能很彪悍）
    }

    @Test
    public void testTimSort(){
        sort = new TimSort();
    }

	private void printArray(int[] printArray){
        System.out.print("[");
        for (int a:printArray){
			System.out.print(a + ",");
		}
        System.out.println("]");
	}
	
	private void validate(Object[] array){
		for(int i = 0; i < array.length - 1; i++){
			Assert.assertTrue(((Comparable) array[i]).compareTo(array[i + 1]) <= 0);
		}
	}
}
