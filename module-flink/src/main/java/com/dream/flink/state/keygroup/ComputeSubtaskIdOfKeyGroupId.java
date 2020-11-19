package com.dream.flink.state.keygroup;

import org.apache.flink.runtime.state.KeyGroupRangeAssignment;

/**
 * @author fanrui03
 * @date 2020/11/19 15:34
 */
public class ComputeSubtaskIdOfKeyGroupId {

    public static void main(String[] args) {
        int maxParallelism = 8192;
        int parallelism = 3000;
        int keyGroupId = 7408;
        int subtaskIndex = KeyGroupRangeAssignment.computeOperatorIndexForKeyGroup(
                maxParallelism, parallelism, keyGroupId);
        System.out.println(subtaskIndex);
    }

}
