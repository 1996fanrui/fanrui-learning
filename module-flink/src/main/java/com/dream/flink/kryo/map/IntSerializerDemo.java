package com.dream.flink.kryo.map;

import org.apache.flink.util.Preconditions;

import java.util.Random;

public class IntSerializerDemo {

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            testInt(random.nextInt());
        }
    }

    private static void testInt(int x) {
        Preconditions.checkState(x == convertBytesToInt(convertIntToBytes(x)));
    }

    private static byte[] convertIntToBytes(int value) {
        byte[] bytes = new byte[4];
        new Random().nextBytes(bytes);
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (value >>> (8 * (3 - i)));
        }
        return bytes;
    }

    private static long convertBytesToInt(byte[] bytes) {
        int value = 0x0000;
        for (int i = 0; i < 4; i++) {
            byte data = bytes[i];
            value |= ((long) (data & 0xff)) << (8 * (3 - i));
        }
        return value;
    }

}
