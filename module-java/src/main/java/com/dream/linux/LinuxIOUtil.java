package com.dream.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LinuxIOUtil {

  public static HashSet<DiskIOUsage> getDiskIOUsageSet() {
    Map<String, Float> diskIOUsageMap = LinuxIOUtil.getDiskIOUsageMap();
    HashSet<DiskIOUsage> result = new HashSet<>();
    for (Map.Entry<String, Float> entry : diskIOUsageMap.entrySet()) {
      LinuxIOUtil.DiskIOUsage diskIOUsage = new LinuxIOUtil.DiskIOUsage(entry.getKey(), entry.getValue());
      System.out.println(diskIOUsage);
      result.add(diskIOUsage);
    }
    return result;
  }

  public static class DiskIOUsage implements Comparable<DiskIOUsage> {

    String device;
    float ioUsage;

    public DiskIOUsage(String device, float ioUsage) {
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
      return Float.compare(this.ioUsage, other.ioUsage);
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
  public static Map<String, Float> getDiskIOUsageMap() {
    Map<String, Float> deviceIOUsage = getDeviceIOUsage();
    Map<String, String> deviceDiskMapping = getDeviceDiskMapping();

    Map<String, Float> result = new HashMap<>();

    for (Map.Entry<String, Float> entry : deviceIOUsage.entrySet()) {
      String device = entry.getKey();
      String disk = deviceDiskMapping.get(device);
      if (disk == null) {
        System.out.println("device: " + device + " not found match disk");
        continue;
      }
      Float ioUsage = entry.getValue();
      System.out.println("device: " + device + " , IOUsage:" + ioUsage);
      result.put(disk, ioUsage);
    }
    return result;
  }

  /**
   * @return key 表示 哪个盘 device，value 表示当前盘的 io 使用率
   */
  public static Map<String, Float> getDeviceIOUsage() {
    System.out.println("开始收集磁盘IO使用率");
    float ioUsage = 0.0f;
    Process pro = null;
    Runtime r = Runtime.getRuntime();

    Map<String, Float> deviceUsage = new HashMap<>();

    try {
      String command = "iostat -x -k 1 10";
      pro = r.exec(command);
      BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
      String line = null;
      Map<String, Float> deviceUsageSum = new HashMap<>();
      int count = 0;
      while ((line = in.readLine()) != null) {
        String[] temp = line.split("\\s+");
        // 过滤其他文本
        if (temp.length != 14) {
          continue;
        }

        // 过滤表头
        if ("Device:".equals(temp[0])) {
          count++;
          continue;
        }

        // 过滤第一次数据采集，第一次采集一般误差较大
        if (count <= 1) {
          continue;
        }

        float util = Float.parseFloat(temp[13]);

        deviceUsageSum.put(temp[0],
            deviceUsageSum.getOrDefault(temp[0], 0.0f) + util);
      }

      for (Map.Entry<String, Float> entry : deviceUsageSum.entrySet()) {
        String device = entry.getKey();
        float usage = entry.getValue() / (count - 1);
        System.out.println("Device:" + device + ", io usage :" + usage);
        deviceUsage.put(device, usage);
      }
      in.close();
      pro.destroy();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("IoUsage发生InstantiationException. " + e.getMessage());
    }

    return deviceUsage;
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
