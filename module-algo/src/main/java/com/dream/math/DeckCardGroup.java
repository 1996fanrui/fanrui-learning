package com.dream.math;

/**
 * @author fanrui
 * 卡牌分组
 * LeetCode 914：https://leetcode-cn.com/problems/x-of-a-kind-in-a-deck-of-cards/
 */
public class DeckCardGroup {

    public boolean hasGroupsSizeX(int[] deck) {
        if (deck == null || deck.length <= 1) {
            return false;
        }
        int[] count = new int[10000];

        // 记录所有数据出现的次数
        for (int i = 0; i < deck.length; i++) {
            count[deck[i]]++;
        }

        int gcd = -1;
        // 遍历后续所有元素，求所有个数的最大公约数
        for (int i = 0; i < count.length; i++) {
            if (count[i] == 0) {
                continue;
            } else if (count[i] == 1) {
                return false;
            }

            // 第一次出现不为 0 的频率，初始化 gcd
            if (gcd == -1) {
                gcd = count[i];
            } else {
                gcd = gcd(gcd, count[i]);
                if (gcd < 2) {
                    return false;
                }
            }
        }
        return gcd > 1;
    }


    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

}
