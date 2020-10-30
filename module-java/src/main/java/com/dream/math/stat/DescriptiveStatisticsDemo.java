package com.dream.math.stat;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @author fanrui03
 * @date 2020/9/17 11:10
 */
public class DescriptiveStatisticsDemo {

    public static void main(String[] args) {
        DescriptiveStatistics stat = new DescriptiveStatistics();
        for (double i = 0; i < 100.1; i++) {
            stat.addValue(i);
        }
        System.out.println("max : " + stat.getMax());
        System.out.println("mean : " + stat.getMean());
        System.out.println("min : " + stat.getMin());
        System.out.println("p50 : " + stat.getPercentile(50));
        System.out.println("p90 : " + stat.getPercentile(90));
        System.out.println("p99 : " + stat.getPercentile(99));
    }

}
