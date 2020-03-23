package com.dream.math;

import java.util.HashMap;
import java.math.BigDecimal;


/**
 * @author fanrui
 * 直线上较多的点数
 * LeetCode 149： https://leetcode-cn.com/problems/max-points-on-a-line/
 */
public class MaxPointsOnLine {

    private int[][] points = null;

    public int maxPoints(int[][] points) {

        if (points == null || points.length == 0) {
            return 0;
        }
        if (points.length <= 2) {
            return points.length;
        }
        this.points = points;

        // 遍历所有点，求第 i 个点构成的最大 count
        int res = 1;
        for (int i = 0; i < points.length; i++) {
            res = Math.max(maxPointOnI(i), res);
        }

        return res;
    }


    // map 中存储 斜率 对应的个数
    HashMap<BigDecimal, Integer> map = new HashMap<>();

    // 找出第 i 个点与 后续所有点在同一条直线能构成的点的个数
    private int maxPointOnI(int i) {
        // 当前与 i 位置的点重复的点有几个，默认就是 i 位置一个
        int duplicate = 1;

        // 存储当前 i 位置 ，对应的最大 count 数
        int count = 0;

        // 存储竖直线上的点个数，不包含自己，自己保存在 duplicate
        int upright = 0;

        map.clear();

        int x1 = points[i][0];
        int y1 = points[i][1];

        // 遍历 后续所有点，求最大的 count
        for (int j = i + 1; j < points.length; j++) {
            int x2 = points[j][0];
            int y2 = points[j][1];

            // 同一个点
            if (x1 == x2 && y1 == y2) {
                duplicate++;
            } else if (x2 == x1) {
                // 同一个竖线上的点
                upright++;
                count = Math.max(count, upright);
            } else {
                BigDecimal slope = new BigDecimal(Double.toString(y2 - y1))
                        .divide(new BigDecimal(Double.toString(x2 - x1)), 20, BigDecimal.ROUND_HALF_UP);
//                double slope = 1.0 * (y2 - y1) / (x2 - x1) + 0.0;
                int slopeCount = map.getOrDefault(slope, 0) + 1;
                map.put(slope, slopeCount);
                count = Math.max(count, slopeCount);
            }
        }
        return count + duplicate;
    }


    public static void main(String[] args) {
//        int i = new MaxPointsOnLine().maxPoints(new int[][]{{2, 3}, {3, 3}, {5, 3}, {-5, 3}, {6, 3}});
        int i = new MaxPointsOnLine().maxPoints(new int[][]{{0, 0}, {94911151, 94911150}, {94911152, 94911151}});
        System.out.println(i);
    }


    // 3
//    public int maxPoints(int[][] points) {
//
//        if (points == null || points.length == 0) {
//            return 0;
//        }
//        if (points.length == 1) {
//            return 1;
//        }
//
//        //  存储每条线上对应多少对数据
//        HashMap<Point, HashSet<Integer>> map = new HashMap<>();
//        for (int i = 0; i < points.length; i++) {
//            for (int j = i + 1; j < points.length; j++) {
//                Point point = genPoint(points, i, j);
//                HashSet<Integer> set;
//                if (map.containsKey(point)) {
//                    set = map.get(point);
//                } else {
//                    set = new HashSet<>();
//                    map.put(point, set);
//                }
//                set.add(i);
//                set.add(j);
//            }
//        }
//
//        int res = 0;
//        for (HashMap.Entry<Point, HashSet<Integer>> entry : map.entrySet()) {
//            res = Math.max(res, entry.getValue().size());
//        }
//        return res;
//    }
//
//    // 计算 i 和j 两个点对应的 a 和 b ：y = ax +b
//    private Point genPoint(int[][] points, int i, int j) {
//        double a = 1.0 * (points[j][1] - points[i][1]) / (points[j][0] - points[i][0]);
//        double b = points[j][1] - a * points[j][0];
//        return new Point(a, b);
//    }
//
//
//    public class Point {
//        double a;
//        double b;
//
//        public Point(double a, double b) {
//            this.a = a;
//            this.b = b;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) {
//                return true;
//            }
//            if (o == null || getClass() != o.getClass()) {
//                return false;
//            }
//            Point point = (Point) o;
//            return a == point.a &&
//                    b == point.b;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(a, b);
//        }
//    }
}
