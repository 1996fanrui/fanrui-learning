package com.dream.back.track;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 93  复原 IP 地址
 * https://leetcode.cn/problems/restore-ip-addresses/description/
 */
public class RestoreIpAddresses {

    public List<String> restoreIpAddresses(String s) {
        List<String> result = new ArrayList<>();
        recursion(s, new LinkedList<>(), result, 0);
        return result;
    }

    public void recursion(String s, LinkedList<String> path, List<String> result, int index) {
        if (path.size() == 4) {
            if (index == s.length()) {
                result.add(String.join(".", path));
            }
            return;
        }

        // 三种长度，且不能超过 字符串 的长度
        for (int i = 1; i <= 3 && index + i <= s.length(); i++) {
            if (i > 1 && s.charAt(index) == '0') {
                return;
            }
            String cur = s.substring(index, index + i);
            int curInt = Integer.parseInt(cur);
            if (0 <= curInt && curInt <= 255) {
                path.add(cur);
                recursion(s, path, result, index + i);
                path.removeLast();
            }
        }
    }

}
