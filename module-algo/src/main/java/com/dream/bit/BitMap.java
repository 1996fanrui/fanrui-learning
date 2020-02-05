package com.dream.bit;

/**
 * @author fanrui
 * @time 2019-04-15 00:42:59
 */
public class BitMap {
    private byte[] bytes;
    private int nbits;

    public BitMap(int nbits) {
        this.nbits = nbits;
        this.bytes = new byte[nbits/8+1];
    }

    public void set(int k) {
        if (k > nbits) {
            return;
        }
        int byteIndex = k / 8;
        int bitIndex = k % 8;
        bytes[byteIndex] |= (1 << bitIndex);
    }

    public boolean get(int k) {
        if (k > nbits) {
            return false;
        }
        int byteIndex = k / 8;
        int bitIndex = k % 8;
        return (bytes[byteIndex] & (1 << bitIndex)) != 0;
    }
}
