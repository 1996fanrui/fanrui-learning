package com.dream.HyperLogLog;

public class HyperLogLog {

    private final RegisterSet registerSet;
    private final int log2m;   //log(m)
    private final double alphaMM;


    /**
     *
     *  rsd = 1.04/sqrt(m)
     * @param rsd  相对标准偏差
     */
    public HyperLogLog(double rsd) {
        this(log2m(rsd));
    }

    /**
     * rsd = 1.04/sqrt(m)
     * m = (1.04 / rsd)^2
     * @param rsd 相对标准偏差
     * @return
     */
    private static int log2m(double rsd) {
        return (int) (Math.log((1.106 / rsd) * (1.106 / rsd)) / Math.log(2));
    }

    private static double rsd(int log2m) {
        return 1.106 / Math.sqrt(Math.exp(log2m * Math.log(2)));
    }


    /**
     * accuracy = 1.04/sqrt(2^log2m)
     *
     * @param log2m
     */
    public HyperLogLog(int log2m) {
        this(log2m, new RegisterSet(1 << log2m));
    }

    /**
     *
     * @param registerSet
     */
    public HyperLogLog(int log2m, RegisterSet registerSet) {
        this.registerSet = registerSet;
        this.log2m = log2m;
        int m = 1 << this.log2m; //从log2m中算出m

        alphaMM = getAlphaMM(log2m, m);
    }


    public boolean offerHashed(int hashedValue) {
        // j 代表第几个桶,取hashedValue的前log2m位即可
        // j 介于 0 到 m
        final int j = hashedValue >>> (Integer.SIZE - log2m);
        // r代表 除去前log2m位剩下部分的前导零 + 1
        final int r = Integer.numberOfLeadingZeros((hashedValue << this.log2m) | (1 << (this.log2m - 1)) + 1) + 1;
        return registerSet.updateIfGreater(j, r);
    }

    /**
     * 添加元素
     * @param o  要被添加的元素
     * @return
     */
    public boolean offer(Object o) {
        final int x = MurmurHash.hash(o);
        return offerHashed(x);
    }


    public long cardinality() {
        double registerSum = 0;
        int count = registerSet.count;
        double zeros = 0.0;
        //count是桶的数量
        for (int j = 0; j < registerSet.count; j++) {
            int val = registerSet.get(j);
            registerSum += 1.0 / (1 << val);
            if (val == 0) {
                zeros++;
            }
        }

        double estimate = alphaMM * (1 / registerSum);

        if (estimate <= (5.0 / 2.0) * count) {  //小数据量修正
            return Math.round(linearCounting(count, zeros));
        } else {
            return Math.round(estimate);
        }
    }


    /**
     *  计算constant常数的取值
     * @param p   log2m
     * @param m   m
     * @return
     */
    protected static double getAlphaMM(final int p, final int m) {
        // See the paper.
        switch (p) {
            case 4:
                return 0.673 * m * m;
            case 5:
                return 0.697 * m * m;
            case 6:
                return 0.709 * m * m;
            default:
                return (0.7213 / (1 + 1.079 / m)) * m * m;
        }
    }

    /**
     *
     * @param m   桶的数目
     * @param V   桶中0的数目
     * @return
     */
    protected static double linearCounting(int m, double V) {
        return m * Math.log(m / V);
    }

    public static void main(String[] args) {
        HyperLogLog hyperLogLog = new HyperLogLog(0.1325);//64个桶
        //集合中只有下面这些元素
        hyperLogLog.offer("hhh");
        hyperLogLog.offer("mmm");
        hyperLogLog.offer("ccc");
        hyperLogLog.offer("cc");
        hyperLogLog.offer("c");
        hyperLogLog.offer("sdfs");
        hyperLogLog.offer("v");
        hyperLogLog.offer("cccc");
        hyperLogLog.offer("cqcc");
        hyperLogLog.offer("cwcc");
        hyperLogLog.offer("ccec");
        hyperLogLog.offer("ccrc");
        hyperLogLog.offer("cctc");
        hyperLogLog.offer("ccyc");
        hyperLogLog.offer("ccuc");
        //估算基数
        System.out.println(hyperLogLog.cardinality());
    }
}
