package com.dream.tree.unionfind.Island;

/**
 * 数组实现并查集
 */
public class UnionFindSetByArray {

    class UnionFind{

        // count 表示岛的数量
        int count;

        /**
         * 使用一个数组 parent，第 i 位上的元素存储着 j，表示 i 的 parent 是 j
         * 初始化时，每一位都存储自己的下标，即：第 i 位上的元素存储着 i，第 j 位上的元素存储着 j，
         * 来表示每一位都指向自己。
         */
        int[] parent;

        /**
         * rank 数组用来保存集合的高度，第 i 位上元素存储着 j，
         * 表示以 i 为代表节点的集合高度为 j
         */
        int[] rank;


        /**
         * 将二维数组 grid 初始化成 并查集。
         * parent 是一维数组，怎么保存二维数组 grid 中的数据呢？
         * 使用 r*C + c 将二维数组映射到一维数组，
         * C 表示二维数组的列数，r 表示当前处于第几行，c 表示当前处于第几列
         * 假设 grid 是一个 4 行 5 列的二维数组，那么 index = r*C + c= r*5+c 是这么存储的：
         * 第 0 行，r = 0， index = 0*5+c = c
         * 第 1 行，r = 1， index = 1*5+c = 5+c
         * 第 2 行，r = 2， index = 2*5+c = 10+c
         * 第 3 行，r = 3， index = 3*5+c = 15+c
         * 说白了，把二维数组第二行跟在了第一行的后面，第三行跟在第二行后面，依次类推，将二维数组拍平
         * @param grid
         */
        UnionFind(char[][] grid) {

            int R = grid.length;
            int C = grid[0].length;
            parent = new int[C*R];
            rank = new int[C*R];
            int res = 0;
            // 遍历所有元素
            for (int r = 0; r < R; r++) {
                for (int c = 0; c < C; c++) {
                    // 初始化 parent、rank 数组， count 存储 1 的个数
                    if (grid[r][c] == 1) {
                        parent[r*C + c] = r*C + c;
                        rank[r*C + c] = 0;
                        // 初始化后，count 为元素 1 的个数
                        count ++;
                    }
                }
            }
        }

        // 递归查找代表节点，顺便压缩路径
        public int findHead(int i){
            if(i != parent[i]){
                parent[i] = findHead(parent[i]);
            }
            return parent[i];
        }

        /**
         * 合并集合 i 、j
         * @param i
         * @param j
         */
        public void union(int i, int j) {
            int iHead = findHead(i);
            int jHead = findHead(j);
            // 两个元素是一个集合，直接返回
            if(iHead == jHead){
                return;
            }

            // 元素数量少的集合 指向元素数量大的集合，并更新 rank
            if(rank[iHead] < rank[jHead]){
                parent[iHead] = jHead;
            } else if (rank[iHead] > rank[jHead]){
                parent[jHead] = iHead;
            } else {
                parent[iHead] = jHead;
                rank[jHead] += 1;
            }
            // 每合并一次，岛的数量 -1
            count--;
        }

        public int getCount() {
            return count;
        }
    }


    public int numIslands(char[][] grid) {
        if(grid==null || grid.length == 0){
            return 0;
        }

        UnionFind unionFind = new UnionFind(grid);

        int R = grid.length;
        int C = grid[0].length;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                // 遍历所有值为 1 的元素
                if (grid[r][c] == 1) {
                    // 左边如果为 1，与左节点 union
                    if(c > 0 && grid[r][c-1] == 1) {
                        unionFind.union(r*C + c -1,r*C + c);
                    }

                    // 上边如果为 1，与上节点 union
                    if(r > 0 && grid[r-1][c] == 1){
                        unionFind.union((r-1)*C + c,r*C + c);
                    }
                }
            }
        }
        return unionFind.getCount();
    }


    public static void main(String[] args) {
        UnionFindSetByArray unionFindSetByArray = new UnionFindSetByArray();

        char[][] m1 = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 1, 1, 1, 0, 1, 1, 1, 0 },
                        { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                        { 0, 1, 1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(unionFindSetByArray.numIslands(m1));

        char[][] m2 = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 1, 1, 1, 1, 1, 1, 1, 0 },
                        { 0, 1, 1, 1, 0, 0, 0, 1, 0 },
                        { 0, 1, 1, 0, 0, 0, 1, 1, 0 },
                        { 0, 0, 0, 0, 0, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 1, 1, 1, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, };
        System.out.println(unionFindSetByArray.numIslands(m2));
        char[][] m3 = { { 1, 1, 1, 1, 0 },
                        { 1, 1, 0, 1, 0 },
                        { 1, 1, 0, 0, 0 },
                        { 0, 0, 0, 0, 0 }, };
        System.out.println(unionFindSetByArray.numIslands(m3));
        char[][] m4 = {};
        System.out.println(unionFindSetByArray.numIslands(m4));
    }

}
