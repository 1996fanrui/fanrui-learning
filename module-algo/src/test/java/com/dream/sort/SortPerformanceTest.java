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
public class SortPerformanceTest {
//    private int arrayCount = 10000;
//    private int arrayLength = 500;
//    private int arrayCount = 500;
//    private int arrayLength = 10000;
    private int arrayCount = 50;
    private int arrayLength = 100000;
    private int[][] array;
    private ISort sort;
    private long startTime;
    private long endTime;

    @Before
    public void before() {
        System.out.println( "before..." );
        array = new int[arrayCount][arrayLength];

        for (int i=0; i<arrayCount; i++ ){
            for ( int j = 0; j < arrayLength; j++ ) {
                array[i][j] = (int)(100 * Math.random());
//                int temp = 0;
//                if ( j % 1000 == 0 ){
//                    temp = (int)(10000000 * Math.random());
//                }
//                array[i][j] = temp + j%1000;
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
	public void testBubbleSort(){
        sort = new BubbleSort();        // 数组长度变成 500，10000个数组 2000ms
	}

    @Test
    public void testInsertSort(){
        // 插入排序性能要比冒泡排序快5倍以上
        sort = new InsertSort();    // 数组长度变成 500，10000个数组 300ms
    }

    @Test
    public void testBinaryInsertSort(){
        sort = new BinaryInsertSort();    // 数组长度变成 500，10000个数组 300ms
    }


    @Test
    public void testPairInsertSort(){
        sort = new PairInsertSort();
    }

	@Test
	public void testSelectionSort(){
		sort = new SelectionSort(); //  数组长度变成 500，10000个数组 800ms
	}

	@Test
	public void testMergeSort(){
		sort = new MergeSort();     // 数组长度变成 500，10000个数组 400ms（如果数组长度变成100000，50个数组，那么归并会比插入排序彪悍很多）
	}

    @Test
    public void testQuickSort(){
        sort = new QuickSort();     // 数组长度变成 500，10000个数组 200ms（如果数组长度变成100000，50个数组，那么快排性能很彪悍）
    }

    @Test
    public void testQuick3WaySort(){
        // 数组长度变成 500，10000个数组 200ms（如果数组长度变成100000，50个数组，那么快排性能很彪悍）
        // 当数值重复较多时，三路快排会比普通快排高效很多
        sort = new Quick3WaySort();
    }

    @Test
    public void testCountingSort(){
        sort = new CountingSort();      // 10000个 长度为50000的数组，排序2468ms
    }


    @Test
    public void testArraysSort(){
        sort = new ArraysSort();
    }


	private void printArray(int[] printArray){
        System.out.print("[");
        for (int a:printArray){
			System.out.print(a + ",");
		}
        System.out.println("]");
	}
	
	private void validate(int[] array){
		for(int i = 0; i < array.length - 1; i++){
			Assert.assertTrue(array[i] <= array[i + 1]);
		}
	}
}
