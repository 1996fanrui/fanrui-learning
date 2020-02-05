package com.dream.sort;

/**
 * 计数排序
 * @author fanrui
 * @time  2019-03-22 14:40:38
 * @see ISort
 * @see SortTest
 */
public class CountingSort implements ISort  {

    @Override
    public void sort(int[] array) {
        if (array.length <= 1) {
          return;
        }

        // 查找数组中数据的范围
        int max = array[0];
        for (int i = 1; i < array.length; ++i) {
            if (max < array[i]) {
                max = array[i];
            }
        }

        // 申请一个计数数组c，下标大小[0,max]
        int[] c = new int[max + 1];
        for (int i = 0; i < max + 1; ++i) {
            c[i] = 0;
        }

        // 计算每个元素的个数，放入c中
        for (int i = 0; i < array.length; ++i) {
            c[array[i]]++;
        }


        // 依次累加
        for (int i = 1; i < max + 1; ++i) {
            c[i] = c[i-1] + c[i];
        }

        // 临时数组r，存储排序之后的结果
        int[] r = new int[array.length];
        // 计算排序的关键步骤了，有点难理解
        for (int i = array.length - 1; i >= 0; --i) {
            int index = c[array[i]]-1;
            r[index] = array[i];
            c[array[i]]--;
        }

        // 将结果拷贝会a数组
        for (int i = 0; i < array.length; ++i) {
            array[i] = r[i];
        }

        /**
         *  下面注释的代码是自己优化的代码，效率是上述的三倍左右
         *  但是当真正的数据 能映射到 m个基数上，但是通过基数不能推导出真正数据时，下面方法行不通
         *  王争老师的代码具有普遍性
          */
//        int j=0;
//        for( int i = 0; i< c.length; ++i ){
//            for ( int k = 0; k < c[i];k++  ){
//                array[j++] = i;
//            }
//        }
    }

}
