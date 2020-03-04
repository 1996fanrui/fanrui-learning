package com.dream.tree.algo;


import java.util.*;

/**
 * 天际线问题
 * LeetCode 链接：https://leetcode-cn.com/problems/the-skyline-problem/
 *
 */
public class TheSkyline {

    public List<List<Integer>> getSkyline(int[][] buildings) {

        // result 对应的 map
        List<List<Integer>> resultList = new ArrayList<>();
        if(buildings == null || buildings.length == 0){
            return resultList;
        }

        Action[] actions = new Action[2*buildings.length];

        for (int i = 0; i < buildings.length; i++) {
            actions[2*i] = new Action(buildings[i][0], true, buildings[i][2]);
            actions[2*i+1] = new Action(buildings[i][1], false, buildings[i][2]);
        }
        Arrays.sort(actions);

        // 当前所有楼层的 map，key 为楼层高度，value 为楼层发生了几次
        TreeMap<Integer, Integer> floorCountMap = new TreeMap<>();

        // key 是 x 坐标， value 是对应的 最高高度。
        // 如果从 x 位置降到了 0，那么最高高度是 0，而不是 矩形高度
        TreeMap<Integer, Integer> resultMap = new TreeMap<>();


        for (Action curAction: actions) {
            // 上楼
            if(curAction.up) {
                // 当前楼层 +1
                floorCountMapIncrement(floorCountMap, curAction.height);
            } else {
                // 下楼
                // 当前楼层 - 1
                floorCountMapDecrement(floorCountMap, curAction.height);
            }
            // 将每个 位置上，最高的点保存起来
            resultMap.put(curAction.x, getCurHighestFloor(floorCountMap));
        }

        int curHeight = 0;

        for (Map.Entry<Integer,Integer> entry: resultMap.entrySet()) {
            int height = entry.getValue();
            // 把相邻的高度相邻的楼删掉，剩余的添加到 list 中
            if(height != curHeight){
                resultList.add(Arrays.asList(entry.getKey(), height));
                curHeight = height;
            }
        }

        return resultList;
    }


    private int getCurHighestFloor(TreeMap<Integer, Integer> floorCountMap) {
        if(floorCountMap.isEmpty()){
            return 0;
        } else{
            return floorCountMap.lastKey();
        }
    }


    // floorCountMap 对应 key 的 value + 1
    private static void floorCountMapIncrement(TreeMap<Integer, Integer> floorCountMap, int floor){
        int count = floorCountMap.getOrDefault(floor,0);
        floorCountMap.put(floor, ++count);
    }

    /**
     * floorCountMap 对应 key 的 value - 1，
     * 如果 value 已经是 1，则remove
     */
    private static void floorCountMapDecrement(TreeMap<Integer, Integer> floorCountMap, int floor){
        if(floorCountMap.containsKey(floor)){
            Integer count = floorCountMap.get(floor);
            if(count == 1){
                floorCountMap.remove(floor);
            } else {
                floorCountMap.put(floor, --count);
            }
        }
    }


    private static class Action implements Comparable{
        // 横坐标
        int x;
        // 行为，上 还是下， true 表示上，flase 表示下
        boolean up;
        // 楼层
        int height;

        public Action(int x, boolean up, int height) {
            this.x = x;
            this.up = up;
            this.height = height;
        }

        @Override
        public int compareTo(Object o) {
            if(!(o instanceof Action)){
                return 0;
            }

            Action obj = (Action)o;

            if(this.x != obj.x){
                return this.x - obj.x;
            }

            if(this.up != obj.up){
                return up ? -1 : 1;
            }
            return 0;
        }
    }


    public static void main(String[] args) {
        TheSkyline theSkyline = new TheSkyline();
//        System.out.println(theSkyline.getSkyline(
//                new int[][]{{2,9,10},{3,7,15},{5,12,12},{15,20,10},{19,24,8}}));


        System.out.println(theSkyline.getSkyline(
                new int[][]{{0,2,3},{2,5,3}}));


        System.out.println(theSkyline.getSkyline(
                new int[][]{{2,4,7},{2,4,5},{2,4,6}}));


//        System.out.println(new Action(2,true).compareTo(new Action(1,true)));
//        System.out.println(new Action(2,true).compareTo(new Action(2,true)));
//        System.out.println(new Action(2,true).compareTo(new Action(2,false)));
    }

}
