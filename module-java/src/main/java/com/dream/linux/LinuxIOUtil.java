package com.dream.linux;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class LinuxIOUtil {

    public static HashSet<DiskIOUsage> getDiskIOUsageSet() {
        Map<String, DescriptiveStatistics> diskIOUsageMap = LinuxIOUtil.getDiskIOUsageMap();
        HashSet<DiskIOUsage> result = new HashSet<>();
        for (Map.Entry<String, DescriptiveStatistics> entry : diskIOUsageMap.entrySet()) {
            LinuxIOUtil.DiskIOUsage diskIOUsage = new LinuxIOUtil.DiskIOUsage(
                entry.getKey(), entry.getValue().getMean());
            System.out.println(diskIOUsage);
            result.add(diskIOUsage);
        }
        return result;
    }

    public static class DiskIOUsage implements Comparable<DiskIOUsage> {

        String device;
        double ioUsage;

        public DiskIOUsage(String device, double ioUsage) {
            this.device = device;
            this.ioUsage = ioUsage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DiskIOUsage that = (DiskIOUsage) o;
            return Objects.equals(device, that.device);
        }

        @Override
        public int hashCode() {
            return Objects.hash(device);
        }

        @Override
        public int compareTo(DiskIOUsage other) {
            return Double.compare(this.ioUsage, other.ioUsage);
        }

        @Override
        public String toString() {
            return "DiskIOUsage{" +
                "device='" + device + '\'' +
                ", ioUsage=" + ioUsage +
                '}';
        }
    }

    /**
     * 获取每个 disk 的 io 使用率
     *
     * @return
     */
    public static Map<String, DescriptiveStatistics> getDiskIOUsageMap() {
        Map<String, DescriptiveStatistics> deviceIOUsage = getDeviceIOUsage();
        Map<String, String> deviceDiskMapping = getDeviceDiskMapping();

        Map<String, DescriptiveStatistics> result = new HashMap<>();

        for (Map.Entry<String, DescriptiveStatistics> entry : deviceIOUsage.entrySet()) {
            String device = entry.getKey();
            String disk = deviceDiskMapping.get(device);
            if (disk == null) {
                System.out.println("device: " + device + " not found match disk");
                continue;
            }
            DescriptiveStatistics ioStat = entry.getValue();
            System.out.println("device: " + device + " , IOUsage:" + ioStat.getMean());
            result.put(disk, ioStat);
        }
        return result;
    }

    /**
     * @return key 表示 哪个盘 device，value 表示当前盘的 io 使用率
     */
    public static Map<String, DescriptiveStatistics> getDeviceIOUsage() {
        System.out.println("开始收集磁盘IO使用率");
        float ioUsage = 0.0f;
        Process pro = null;
        Runtime r = Runtime.getRuntime();

        Map<String, DescriptiveStatistics> deviceIOStat = new HashMap<>();

        try {
            String command = "iostat -x -k 1 101";
            pro = r.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            int count = 0;
            while ((line = in.readLine()) != null) {
                String[] temp = line.split("\\s+");
                // 过滤其他文本
                if (temp.length != 14) {
                    continue;
                }

                // 过滤表头
                String device = temp[0];
                if ("Device:".equals(device)) {
                    count++;
                    continue;
                }

                // 过滤第一次数据采集，第一次采集一般误差较大
                if (count <= 1) {
                    continue;
                }

                float util = Float.parseFloat(temp[13]);

                DescriptiveStatistics ioStat = deviceIOStat.get(device);
                if (ioStat == null) {
                    ioStat = new DescriptiveStatistics();
                    deviceIOStat.put(device, ioStat);
                }
                ioStat.addValue(util);
            }

            in.close();
            pro.destroy();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IoUsage发生InstantiationException. " + e.getMessage());
        }

        return deviceIOStat;
    }

    /**
     * 获取 device 与 disk 的映射关系
     *
     * @return
     * @throws IOException
     */
    public static Map<String, String> getDeviceDiskMapping() {
        HashMap<String, String> result = new HashMap<>();

        Process pro = null;
        Runtime r = Runtime.getRuntime();

        try {
            String command = "df -h";
            pro = r.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            Map<String, Float> deviceUsage = new HashMap<>();
            int count = 0;
            while ((line = in.readLine()) != null) {
                String[] temp = line.split("\\s+");
                // 过滤不符合规则的行
                if (temp.length != 6) {
                    continue;
                }

                // 只要符合规则的 disk
                String device = temp[0];
                String disk = temp[5];
                if (device.startsWith("/dev/sd") &&
                    disk.startsWith("/media/disk")) {
                    device = device.substring(5);
                    System.out.println("device : " + device + "  , disk:" + disk);
                    result.put(device, disk);
                }
            }
            in.close();
            pro.destroy();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IoUsage发生InstantiationException. " + e.getMessage());
        }
        return result;
    }

}
