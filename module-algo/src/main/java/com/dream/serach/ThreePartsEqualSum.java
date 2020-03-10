package com.dream.serach;

/**
 * @author fanrui
 * @time 2020-03-11 00:22:37
 * 将数组分成和相等的三个部分
 * LeetCode 1013：https://leetcode-cn.com/problems/partition-array-into-three-parts-with-equal-sum/
 *
 */
public class ThreePartsEqualSum {


    public boolean canThreePartsEqualSum(int[] A) {
        if(A == null && A.length < 3){
            return false;
        }

        int[] sum = new int[A.length];
        sum[0] = A[0];

        for (int i = 1; i < A.length; i++) {
            sum[i] = sum[i-1] + A[i];
        }

        if(sum[A.length-1] % 3 != 0){
            return false;
        }

        int point = sum[A.length-1] / 3;

        int left = 0;
        int right = A.length - 2;
        while (left < right){
            if(sum[left] == point && sum[right] == 2*point){
                return true;
            }
            if(sum[left] != point){
                left++;
            }

            if(sum[right] != 2*point){
                right--;
            }
        }


        return false;
    }
}
