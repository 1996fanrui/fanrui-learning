package com.dream.HyperLogLog;

public class RegisterSet {

    public final static int LOG2_BITS_PER_WORD = 6;  //2的6次方是64
    public final static int REGISTER_SIZE = 5;     //每个register占5位,代码里有一些细节涉及到这个5位，所以仅仅改这个参数是会报错的

    public final int count;
    public final int size;

    private final int[] M;

    //传入m
    public RegisterSet(int count) {
        this(count, null);
    }

    public RegisterSet(int count, int[] initialValues) {
        // count 个 register
        this.count = count;

        if (initialValues == null) {
            /**
             * 分配(m / 6)个int给M
             *
             * 因为一个register占五位，所以每个int（32位）有6个register
             */
            this.M = new int[getSizeForCount(count)];
        } else {
            this.M = initialValues;
        }
        //size代表RegisterSet所占字的大小
        this.size = this.M.length;
    }

    public static int getBits(int count) {
        return count / LOG2_BITS_PER_WORD;
    }

    public static int getSizeForCount(int count) {
        int bits = getBits(count);
        if (bits == 0) {
            return 1;
        } else if (bits % Integer.SIZE == 0) {
            return bits;
        } else {
            return bits + 1;
        }
    }

    public void set(int position, int value) {
        int bucketPos = position / LOG2_BITS_PER_WORD;
        int shift = REGISTER_SIZE * (position - (bucketPos * LOG2_BITS_PER_WORD));
        this.M[bucketPos] = (this.M[bucketPos] & ~(0x1f << shift)) | (value << shift);
    }

    public int get(int position) {
        int bucketPos = position / LOG2_BITS_PER_WORD;
        int shift = REGISTER_SIZE * (position - (bucketPos * LOG2_BITS_PER_WORD));
        return (this.M[bucketPos] & (0x1f << shift)) >>> shift;
    }

    public boolean updateIfGreater(int position, int value) {
        int bucket = position / LOG2_BITS_PER_WORD;    //M下标
        int shift = REGISTER_SIZE * (position - (bucket * LOG2_BITS_PER_WORD));  //M偏移
        int mask = 0x1f << shift;      //register大小为5位

        // 这里使用long是为了避免int的符号位的干扰
        long curVal = this.M[bucket] & mask;
        long newVal = value << shift;
        if (curVal < newVal) {
            //将M的相应位置为新的值
            this.M[bucket] = (int) ((this.M[bucket] & ~mask) | newVal);
            return true;
        } else {
            return false;
        }
    }

    public void merge(RegisterSet that) {
        for (int bucket = 0; bucket < M.length; bucket++) {
            int word = 0;
            for (int j = 0; j < LOG2_BITS_PER_WORD; j++) {
                int mask = 0x1f << (REGISTER_SIZE * j);

                int thisVal = (this.M[bucket] & mask);
                int thatVal = (that.M[bucket] & mask);
                word |= (thisVal < thatVal) ? thatVal : thisVal;
            }
            this.M[bucket] = word;
        }
    }

    int[] readOnlyBits() {
        return M;
    }

    public int[] bits() {
        int[] copy = new int[size];
        System.arraycopy(M, 0, copy, 0, M.length);
        return copy;
    }
}
