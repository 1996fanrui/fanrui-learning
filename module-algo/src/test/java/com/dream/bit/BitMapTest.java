package com.dream.bit;

import bloomfilter.CanGenerateHashFrom;
import bloomfilter.mutable.BloomFilter;


public class BitMapTest {

    @org.junit.Test
    public void test(){
        final int BIT_MAP_NUMBER = 20;
        BitMap bitMap = new BitMap(BIT_MAP_NUMBER);

        bitMap.set(10);


        for(int i=0;i<BIT_MAP_NUMBER;i++){
            System.out.println( i + "  is " +  bitMap.get(i) );
        }
    }

    @org.junit.Test
    public void testBloomFilter(){

        double BLOOM_FPP = 0.0000001d;
        long EXPECTED_INSERTIONS = 1024 * 1024 * 32;

        BloomFilter bloomFilter = BloomFilter.apply(EXPECTED_INSERTIONS,BLOOM_FPP, CanGenerateHashFrom.CanGenerateHashFromByteArray$.MODULE$);
        System.out.println(bloomFilter.numberOfBits());
        System.out.println(bloomFilter.numberOfBits()/8/1024/1024);
        System.out.println(bloomFilter.numberOfHashes());

    }


}
