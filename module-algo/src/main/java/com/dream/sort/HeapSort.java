package com.dream.sort;

/**
 * 堆排序
 */
public class HeapSort implements ISort{

    /**
     * 排序
     * <p>
     * 堆元素是从数组下标0开始
     *
     * @param arr
     */
    @Override
    public void sort(int[] arr) {
        if (arr.length <= 1) {
            return;
        }

        // 1、建堆
        buildHeap(arr);

        // 2、排序
        // heapSize 表示当前 堆的元素个数
        int heapSize = arr.length - 1;
        while (heapSize > 0) {
            // 将堆顶元素（最大）与最后一个元素交换位置
            swap(arr, 0, heapSize);
            // 将剩下元素重新堆化，堆顶元素变成最大元素， 堆中元素个数-1
            heapify(arr, --heapSize, 0);
        }
    }

    /**
     * 建堆
     *
     * @param arr
     */
    private static void buildHeap(int[] arr) {
        // (arr.length - 1) / 2 为最后一个叶子节点的父节点
        // 也就是最后一个非叶子节点，依次堆化直到根节点
        for (int i = (arr.length - 1) / 2; i >= 0; i--) {
            heapify(arr, arr.length - 1, i);
        }
    }

    /**
     * 堆化
     *
     * @param arr 要堆化的数组
     * @param n   堆的元素个数
     * @param i   要堆化的元素下标
     */
    private static void heapify(int[] arr, int n, int i) {
        while (true) {
            // 最大值位置
            int maxPos = i;
            // 与左子节点（i * 2 + 1）比较，获取最大值位置
            if (i * 2 + 1 <= n && arr[i] < arr[i * 2 + 1]) {
                maxPos = i * 2 + 1;
            }
            // 最大值与右子节点（i * 2 + 2）比较，获取最大值位置
            if (i * 2 + 2 <= n && arr[maxPos] < arr[i * 2 + 2]) {
                maxPos = i * 2 + 2;
            }
            // 最大值是当前位置结束循环
            if (maxPos == i) {
                break;
            }
            // 与子节点交换位置
            swap(arr, i, maxPos);
            // 以交换后子节点位置接着往下查找
            i = maxPos;
        }
    }

    private static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }


}