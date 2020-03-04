package com.dream.tree.algo;

import java.util.*;
import java.io.*;

/**
 * @author fanrui
 * 子数组异或和为0的最多划分
 * 牛客链接：https://www.nowcoder.com/practice/77e9828bbe3c4d4a9e0d49cc7537bb6d?tpId=101&tqId=33110&tPage=1&rp=1&ru=/ta/programmer-code-interview-guide&qru=/ta/programmer-code-interview-guide/question-ranking
 *
 */
public class MostEOR {

    public static int mostEOR(int[] arr) {
        int ans = 0;
        int xor = 0;
        int[] dp = new int[arr.length];
        HashMap<Integer, Integer> map = new HashMap<>(arr.length + (arr.length >> 1));
        map.put(0, -1);
        for (int i = 0; i < arr.length; i++) {
            // 计算从 0 到 i 的异或和
            xor ^= arr[i];

            // 找上一个产生同样异或和 的 index，然后 dp 值 +1
            if (map.containsKey(xor)) {
                int pre = map.get(xor);
                dp[i] = pre == -1 ? 1 : (dp[pre] + 1);
            }
            // 还要考虑，是否前一个 index 产生的符合规则的数据更多
            if (i > 0) {
                dp[i] = Math.max(dp[i - 1], dp[i]);
            }

            // 当前 index 更新到 map 中
            map.put(xor, i);
            // 更新最大数组个数
            ans = Math.max(ans, dp[i]);
        }
        return ans;
    }


    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] strs = br.readLine().split(" ");
        int n = Integer.parseInt(strs[0]);
        if (n == 0) {
            System.out.println("0");
        }
        strs = br.readLine().split(" ");
        int[] arr = new int[n];
        for (int i = 0; i < n; i++){
            arr[i] = Integer.parseInt(strs[i]);
        }
        System.out.println(mostEOR(arr));
    }

}
