package com.dream.jmh;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author fanrui03
 * @time 2020-08-17 12:47:49
 */
public class BenchmarkConstants {

    public int setupKeyCount;

    public ArrayList<Long> setupKeys;

    public void constantSetup(int setupKeyCount) {
        this.setupKeyCount = setupKeyCount;

        {
            ArrayList<Long> list = new ArrayList<>(setupKeyCount);
            for (long i = 0; i < setupKeyCount; i++) {
                list.add(i);
            }
            Collections.shuffle(list);
            setupKeys = list;
        }

    }

    static final int randomValueCount = 1_000_000;
    static final ArrayList<Long> randomValues = new ArrayList<>(randomValueCount);

    static {
        for (long i = 0; i < randomValueCount; i++) {
            randomValues.add(i);
        }
        Collections.shuffle(randomValues);
    }

}

