package com.dream.linux;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.*;

/**
 * @author fanrui03
 * @date 2020/9/17 11:42
 * java -cp module-java-1.0-SNAPSHOT-jar-with-dependencies.jar com.dream.linux.IOStat
 */
public class IOStat {

    public static void main(String[] args) {
        String[] paths = new String[]{"/media/disk4/data", "/media/disk5/data", "/media/disk6/data",
            "/media/disk7/data", "/media/disk8/data", "/media/disk9/data", "/media/disk10/data",
            "/media/disk11/data", "/media/disk12/data"};

        Map<String, DescriptiveStatistics> diskIOStatMap = LinuxIOUtil.getDiskIOUsageMap();

        SortedMap<LinuxIOUtil.DiskIOUsage, String> meanMap = new TreeMap<>();
        SortedMap<LinuxIOUtil.DiskIOUsage, String> tp50Map = new TreeMap<>();
        SortedMap<LinuxIOUtil.DiskIOUsage, String> tp60Map = new TreeMap<>();
        SortedMap<LinuxIOUtil.DiskIOUsage, String> tp70Map = new TreeMap<>();

        for (String filePath : paths) {
            int index = filePath.indexOf("/", 9);
            if (index == -1) {
                continue;
            }
            String fileDiskName = filePath.substring(0, index);
            DescriptiveStatistics diskIOStat = diskIOStatMap.get(fileDiskName);
            meanMap.put(new LinuxIOUtil.DiskIOUsage(fileDiskName, diskIOStat.getMean()), filePath);
            tp50Map.put(new LinuxIOUtil.DiskIOUsage(fileDiskName, diskIOStat.getPercentile(50)), filePath);
            tp60Map.put(new LinuxIOUtil.DiskIOUsage(fileDiskName, diskIOStat.getPercentile(60)), filePath);
            tp70Map.put(new LinuxIOUtil.DiskIOUsage(fileDiskName, diskIOStat.getPercentile(70)), filePath);
        }

        System.out.println("meanMap:");
        for (Map.Entry<LinuxIOUtil.DiskIOUsage, String> entry : meanMap.entrySet()) {
            System.out.println("path: " + entry.getValue() + " value : " + entry.getKey().ioUsage);
        }

        System.out.println("tp50Map:");
        for (Map.Entry<LinuxIOUtil.DiskIOUsage, String> entry : tp50Map.entrySet()) {
            System.out.println("path: " + entry.getValue() + " value : " +
                entry.getKey().ioUsage);
        }

        System.out.println("tp60Map:");
        for (Map.Entry<LinuxIOUtil.DiskIOUsage, String> entry : tp60Map.entrySet()) {
            System.out.println("path: " + entry.getValue() + " value : " +
                entry.getKey().ioUsage);
        }

        System.out.println("tp70Map:");
        for (Map.Entry<LinuxIOUtil.DiskIOUsage, String> entry : tp70Map.entrySet()) {
            System.out.println("path: " + entry.getValue() + " value : " +
                entry.getKey().ioUsage);
        }

    }

}
