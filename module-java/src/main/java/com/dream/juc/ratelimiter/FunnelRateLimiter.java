package com.dream.juc.ratelimiter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanrui
 * @time 2020-01-30 11:30:27
 * 漏桶算法的实现:
 * 变量 leftQuota 存储漏桶当前剩余的容量.
 * 每次有请求时，将时间转换为容量.
 * 容量 > 请求数，则容量 - 请求数，并返回 true 。否则 返回 false
 */
public class FunnelRateLimiter {

    static class Funnel {
        /**
         * 总容量
         */
        int capacity;

        /**
         * 漏水速率
         */
        float leakingRate;

        /**
         * 剩余容量
          */
        float leftQuota;

        /**
         * 上次漏水时的时间戳
         */
        long leakingTs;

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }

        /**
         * 重点：当前时间 - 上次漏水的时间，就可以计算出当前需要漏多少水
         * 也就可以计算出来，剩余的
         */
        void makeSpace() {
            long nowTs = System.currentTimeMillis();
            // 计算距离上次漏水的间隔时间，从而得出本次该漏多少水
            long deltaTs = nowTs - leakingTs;
            float deltaQuota = deltaTs * leakingRate;
            if (deltaQuota < 0) { // 间隔时间太长，整数数字过大溢出
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }

            // 漏桶中剩余容量 + 漏掉的水
            this.leftQuota += deltaQuota;

            // 更新漏水时间
            this.leakingTs = nowTs;
            if (this.leftQuota > this.capacity) {
                this.leftQuota = this.capacity;
            }
        }

        boolean watering(int quota) {
            makeSpace();
            if (this.leftQuota >= quota) {
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }

    private Map<String, Funnel> funnels = new HashMap<>();

    public boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", userId, actionKey);
        Funnel funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingRate);
            funnels.put(key, funnel);
        }
        return funnel.watering(1); // 需要1个quota
    }
}
