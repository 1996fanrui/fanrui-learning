package com.dream.window;

/**
 * @author fanrui
 * @time 2020-03-21 23:03:33
 * 无重复字符的最长子串
 * LeetCode 3: https://leetcode-cn.com/problems/longest-substring-without-repeating-characters/
 * 剑指 Offer 48：https://leetcode-cn.com/problems/zui-chang-bu-han-zhong-fu-zi-fu-de-zi-zi-fu-chuan-lcof/
 */
public class LengthOfLongestSubstringWithoutRepeat {

    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        boolean[] set = new boolean[128];
        // 窗口的 左右边界
        int L = 0;
        int R = 0;
        int res = 1;
        set[s.charAt(0)] = true;
        // R 还未到达右边界，则一直扩充，每次循环扩充一个格
        while (R < s.length() - 1) {
            // 下一个格没法扩充，L 向右扩
            while (set[s.charAt(R + 1)]) {
                set[s.charAt(L++)] = false;
            }
            // R 向右扩，且将 R 位置的元素加入到 set 中
            set[s.charAt(++R)] = true;
            res = Math.max(res, R - L + 1);
        }

        return res;
    }

}
