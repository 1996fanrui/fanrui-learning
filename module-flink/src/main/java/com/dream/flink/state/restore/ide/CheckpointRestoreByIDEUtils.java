package com.dream.flink.state.restore.ide;

import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.runtime.instance.SlotSharingGroupId;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.test.util.MiniClusterWithClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author fanrui03
 * @date 2020/11/29 14:19
 * refer: Flink 源码的 {@link ResumeCheckpointManuallyITCase}
 */
public class CheckpointRestoreByIDEUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CheckpointRestoreByIDEUtils.class);

    public static void run(
            @Nonnull StreamGraph streamGraph,
            @Nullable String externalCheckpoint) throws Exception {
        // 根据 StreamGraph 生成 JobGraph
        JobGraph jobGraph = streamGraph.getJobGraph();
        if (externalCheckpoint != null) {
            // 将 Checkpoint 目录设置到 JobGraph 中
            jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(externalCheckpoint));
        }

        // 计算 jobGraph 需要的 slot 个数
        int slotNum = getSlotNum(jobGraph);
        // 初始化 MiniCluster
        ClusterClient<?> clusterClient = initCluster(slotNum);
        // 提交任务到 MiniCluster
        clusterClient.submitJob(jobGraph).get();
    }

    private static int getSlotNum(JobGraph jobGraph) {
        // 保存每个 SlotSharingGroup 需要的 slot 个数
        HashMap<SlotSharingGroupId, Integer> map = new HashMap<>();
        for (JobVertex jobVertex : jobGraph.getVertices()) {
            SlotSharingGroupId slotSharingGroupId = jobVertex.getSlotSharingGroup()
                    .getSlotSharingGroupId();
            int parallelism = jobVertex.getParallelism();
            int oldParallelism = map.getOrDefault(slotSharingGroupId, 0);
            if (parallelism > oldParallelism) {
                map.put(slotSharingGroupId, parallelism);
            }
        }
        // 将所有 SlotSharingGroup 的 slot 个数累加
        int slotNum = 0;
        for (int parallelism : map.values()) {
            slotNum += parallelism;
        }
        LOG.info("slot num : {}", slotNum);
        return slotNum;
    }

    private static ClusterClient<?> initCluster(int slotNum) throws Exception {
        MiniClusterWithClientResource cluster = new MiniClusterWithClientResource(
                new MiniClusterResourceConfiguration.Builder()
                        .setNumberSlotsPerTaskManager(slotNum)
                        .build());
        cluster.before();
        return cluster.getClusterClient();
    }

}
