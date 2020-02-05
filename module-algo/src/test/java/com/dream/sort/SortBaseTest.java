package com.dream.sort;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 排序测试
 * @author fanrui
 * @time 2019-03-21 15:17:25
 * @see ISort
 * @see BubbleSort
 * @see SelectionSort
 * @see MergeSort
 * @see QuickSort
 */
public class SortBaseTest {
	int[] array ;
	ISort sort;

    @Before
    public void before(){
        int[] arrayIni = {10, 9, 8, 7, 6, 5, 4, 4, 1, -1, 2};
		arrayIni = new int[]{ 6, 5, 4, 4, 1, 2, 4, 4, 1, 2, 4, 4, 1, 2, 4, 4, 1, 2, 4, 4, 1, 2};
		arrayIni = new int[]{ 6, 5, 4, 4, 1, 2};
//		arrayIni = new int[]{2,3,4,5,6,7,8,9,0,7};
        array = new int[arrayIni.length];
        for(int i = 0; i < array.length; i++){
            array[i] = arrayIni[i];
        }
    }

    @After
	public void after(){
		sort.sort(array);
		printArray(array);
		validate();
	}
	
	@Test
	public void testBubbleSort(){
		sort = new BubbleSort();
	}

	@Test
	public void testInsertSort(){
		sort = new InsertSort();
	}

	@Test
	public void testBinaryInsertSort(){
		sort = new BinaryInsertSort();
	}

	@Test
	public void testPairInsertSort(){
		sort = new PairInsertSort();
	}


	@Test
	public void testSelectionSort(){
		sort = new SelectionSort();
	}

	@Test
	public void testMergeSort(){
		sort = new MergeSort();
	}

    @Test
    public void testQuickSort(){
        sort = new QuickSort();
    }

    @Test
    public void testQuick3WaySort(){
        sort = new Quick3WaySort();
    }

    @Test
    public void testCountingSort(){
        sort = new CountingSort();
    }


//	@Test
//	public void testInsertOptimizeSort(){
//		sort = new InsertOptimizeSort();
//		sort.sort(array);
//		validate();
//	}

	private void printArray(int[] printArray) {
        System.out.print("[");
        for (int a:printArray){
			System.out.print(a + ",");
		}
        System.out.println("]");
	}
	
	private void validate(){
		for(int i = 0; i < array.length - 1; i++){
			Assert.assertTrue(array[i] <= array[i + 1]);
		}
	}
}
