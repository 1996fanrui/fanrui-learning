package com.dream.linux;

import java.io.File;
import java.util.*;

/**
 * java -cp module-java-1.0-SNAPSHOT.jar com.dream.linux.IOUsage
 */
public class IOUsage {

  public static void main(String[] args) {
//    LinuxIOUtil.getDiskIOUsageSet();
    String dir = "/media/disk5/yarn_data/usercache/dp/appcache/application_1594283756539_12798/flink-io-f857c869-cf34-4af5-b272-638d47fc58bc";
    File file = new File(dir);
    System.out.println(file.getPath());
    System.out.println(file.getAbsolutePath());
//    int index = dir.indexOf("/", 9);
//    System.out.println(dir.substring(0, index));
  }

}



