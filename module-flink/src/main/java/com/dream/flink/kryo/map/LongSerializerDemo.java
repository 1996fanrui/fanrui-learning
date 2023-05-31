package com.dream.flink.kryo.map;

import org.apache.flink.util.Preconditions;

import java.util.Random;

public class LongSerializerDemo {

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            testLong(random.nextLong());
        }
    }

    private static void testLong(long x) {
        Preconditions.checkState(x == convertBytesToLong(convertLongToBytes(x)));
    }

    private static byte[] convertLongToBytes(long value) {
        byte[] bytes = new byte[8];
        new Random().nextBytes(bytes);
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (value >>> (8 * (7 - i)));
        }
        return bytes;
    }

    private static long convertBytesToLong(byte[] bytes) {
        long value = 0x00000000;
        for (int i = 0; i < 8; i++) {
            byte data = bytes[i];
            value |= ((long) (data & 0xff)) << (8 * (7 - i));
        }
        return value;
    }

}
