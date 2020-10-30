package com.dream.linux;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import sun.security.krb5.internal.APRep;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;

/**
 * @author fanrui03
 * @date 2020/9/17 14:39
 * <p>
 * java -cp module-java-1.0-SNAPSHOT-jar-with-dependencies.jar com.dream.linux.RocksDBDiskRebalance
 */
public class RocksDBDiskRebalance {

    private static final String[] ALL_DISK = new String[]{"/media/disk4",
        "/media/disk5", "/media/disk6", "/media/disk7", "/media/disk8",
        "/media/disk9", "/media/disk10", "/media/disk11", "/media/disk12"};

    private static final String BLANK_DIR = "";

    private static final String TARGET = "target";

    private static final int PRE_CREATE_SST_NUM = 10;

    private static final String PROCESS_FILE_NAME = "/tmp/RocksDBDiskRebalance";

    public static void main(String[] args) throws IOException {
        File processFile = new File(PROCESS_FILE_NAME);
        if (processFile.exists()) {
            System.out.println(PROCESS_FILE_NAME + " 文件存在，直接退出进程.");
            return;
        } else {
            boolean newFile = processFile.createNewFile();
            if (!newFile) {
                System.out.println(PROCESS_FILE_NAME + " 文件创建失败，直接退出进程.");
                return;
            }
            processFile.deleteOnExit();
        }

        String yarnAppId = "application_1594283756539_16265";
        start(yarnAppId);
    }

    private static void start(String yarnAppId) {
        Map<File, String> flinkIODirs = getFlinkIODirs(yarnAppId);
        Map<String, DescriptiveStatistics> diskIOUsageMap = LinuxIOUtil.getDiskIOUsageMap();

        for (Map.Entry<File, String> entry : flinkIODirs.entrySet()) {
            System.out.println("flinkIODir: " + entry.getKey() + " value:" + entry.getValue());
        }
        SortedSet<LinuxIOUtil.DiskIOUsage> unknownDirs = new TreeSet<>();
        // key : opDirName, value : targetFile
        Map<String, File> targetMap = new HashMap<>();

        finkTargetAndUnknown(flinkIODirs, diskIOUsageMap, unknownDirs, targetMap);

        printUnknownAndTarget(unknownDirs, targetMap);

        Map<String, String> sourceTargetMap = getSourceTargetMap(flinkIODirs, diskIOUsageMap, unknownDirs, targetMap);

        for (Map.Entry<String, String> entry : sourceTargetMap.entrySet()) {
            doRebalance(entry.getKey(), entry.getValue());
        }
    }

    private static void doRebalance(String sourceDir, String targetDir) {
        cleanTargetExpireSst(sourceDir, targetDir);

        System.out.println("source:" + sourceDir + ", target : " + targetDir);
        File sourceFile = new File(sourceDir);
        File targetFile = new File(targetDir);
        File[] sourceSstFiles = sourceFile.listFiles((dir, name) -> name.endsWith(".sst"));
        File[] targetSstFiles = targetFile.listFiles((dir, name) -> name.endsWith(".sst"));
        if (sourceSstFiles == null || targetSstFiles == null) {
            return;
        }

        // 检查有多少个空的 sst 文件，超过 10 个直接放弃操作
        if (targetSstFiles.length >= PRE_CREATE_SST_NUM) {
            long count = Arrays.stream(targetSstFiles)
                .filter(file -> file.length() == 0)
                .count();
            if (count >= PRE_CREATE_SST_NUM) {
                return;
            }
        }

        int maxSst = Math.max(getMaxSstIndex(sourceSstFiles), getMaxSstIndex(targetSstFiles));
        if (maxSst == -1) {
            return;
        }
        System.out.println("maxSst:" + maxSst);

        // target 中创建文件，source 中创建软链
        for (int i = maxSst + 1; i <= maxSst + 10; i++) {
            if (i % 2 == 0) {
                continue;
            }
            String sstName = String.format("%06d", i) + ".sst";
            String targetSstName = targetDir + "/" + sstName;
            String sourceSstName = sourceDir + "/" + sstName;
            File targetSstFile = new File(targetSstName);
            try {
                boolean createTarget = targetSstFile.createNewFile();
                if (!createTarget) {
                    continue;
                }
                Files.createSymbolicLink(FileSystems.getDefault().getPath(sourceSstName),
                    FileSystems.getDefault().getPath(targetSstName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("create source: " + sourceSstName + " target: " + targetSstName);
        }
    }

    private static void cleanTargetExpireSst(String sourceDir, String targetDir) {

        File sourceFile = new File(sourceDir);
        File targetFile = new File(targetDir);
        File[] sourceSstFiles = sourceFile.listFiles((dir, name) -> name.endsWith(".sst"));
        File[] targetSstFiles = targetFile.listFiles((dir, name) -> name.endsWith(".sst"));
        if (sourceSstFiles == null || targetSstFiles == null) {
            return;
        }

        Map<String, File> targetMap = new HashMap<>();
        for (File targetSstFile : targetSstFiles) {
            targetMap.put(targetSstFile.getName(), targetSstFile);
        }
        for (File sourceSstFile : sourceSstFiles) {
            targetMap.remove(sourceSstFile.getName());
        }
        for (File deleteFile : targetMap.values()) {
            System.out.println("delete: " + deleteFile.getPath() + " will be delete.");
            deleteFile.delete();
        }
    }

    private static int getMaxSstIndex(File[] sstFiles) {
        if (sstFiles == null || sstFiles.length == 0) {
            return -1;
        }

        OptionalInt max = Arrays.stream(sstFiles)
            .map(File::getName)
            .map(sstFileName -> sstFileName.substring(0, 6))
            .mapToInt(Integer::parseInt)
            .max();

        return max.orElse(-1);
    }

    private static void printUnknownAndTarget(SortedSet<LinuxIOUtil.DiskIOUsage> unknownDirs, Map<String, File> targetMap) {
        System.out.println("unknownDirs:");
        for (LinuxIOUtil.DiskIOUsage unknownDir : unknownDirs) {
            System.out.println(unknownDir);
        }
        System.out.println("targetMap:");
        for (Map.Entry<String, File> entry : targetMap.entrySet()) {
            System.out.println(entry.getKey() + "" + entry.getValue());
        }
    }

    private static Map<String, String> getSourceTargetMap(
        Map<File, String> flinkIODirs,
        Map<String, DescriptiveStatistics> diskIOUsageMap,
        SortedSet<LinuxIOUtil.DiskIOUsage> unknownDir,
        Map<String, File> targetMap) {
        Map<String, String> result = new HashMap<>();

        Iterator<LinuxIOUtil.DiskIOUsage> unknownDirIter = unknownDir.iterator();
        for (Map.Entry<File, String> entry : flinkIODirs.entrySet()) {

            String opName = entry.getValue();
            if (BLANK_DIR.equals(opName) ||
                TARGET.equals(opName)) {
                continue;
            }

            String opDir = entry.getKey().getPath();
            String diskName = dirToDisk(opDir);
            DescriptiveStatistics ioStat = diskIOUsageMap.get(diskName);
            double ioUsage = ioStat.getPercentile(70);
            System.out.println("source : " + opDir);

            if (ioUsage > 75 ||
                (ioUsage > 55 && targetMap.containsKey(opName))) {

                File targetFile = targetMap.get(opName);
                String targetDBDir;
                if (targetFile == null) {
                    if (!unknownDirIter.hasNext()) {
                        continue;
                    }
                    LinuxIOUtil.DiskIOUsage next = unknownDirIter.next();
                    targetDBDir = next.device + "/" + TARGET + "/" + opName + "/db";
                    boolean mkdir = new File(targetDBDir).mkdirs();
                    if (!mkdir) {
                        System.out.println(targetDBDir + " mkdir faild.");
                        continue;
                    }
                } else {
                    targetDBDir = targetFile.getPath() + "/" + opName + "/db";
                }

                String sourceDBDir = opDir + "/db";
                result.put(sourceDBDir, targetDBDir);
            } else {
                System.out.println("");
            }
        }
        return result;
    }

    private static void finkTargetAndUnknown(
        Map<File, String> flinkIODirs,
        Map<String, DescriptiveStatistics> diskIOUsageMap,
        SortedSet<LinuxIOUtil.DiskIOUsage> unknownDir,
        Map<String, File> targetMap) {

        for (Map.Entry<File, String> entry : flinkIODirs.entrySet()) {
            String opDir = entry.getKey().getPath();
            String diskName = dirToDisk(opDir);
            DescriptiveStatistics ioStat = diskIOUsageMap.get(diskName);
            double ioUsage = ioStat.getPercentile(60);
            System.out.println("diskName:" + diskName + "   ioUsage:" + ioUsage);
            if (BLANK_DIR.equals(entry.getValue()) && ioUsage < 30) {
                unknownDir.add(new LinuxIOUtil.DiskIOUsage(
                    opDir, ioUsage));
            }

            if (TARGET.equals(entry.getValue())) {
                File targetFile = entry.getKey();
                String[] opFile = targetFile.list();
                if (opFile == null || opFile.length == 0) {
                    continue;
                }
                targetMap.put(opFile[0], targetFile);
            }
        }
    }

    private static String dirToDisk(String dir) {
        int index = dir.indexOf("/", 9);
        if (index == -1) {
            return null;
        }
        return dir.substring(0, index);
    }

    private static Map<File, String> getFlinkIODirs(String yarnAppId) {
        Map<File, String> result = new HashMap<>();
        for (String disk : ALL_DISK) {
            String dir = disk + "/yarn_data/usercache/dp/appcache/" + yarnAppId;
            File file = new File(dir);
            File[] files = file.listFiles((dir1, name) -> name.startsWith("flink-io"));
            if (files == null || files.length == 0) {
                continue;
            }
            for (File flinkIOFile : files) {
                String opDirName = getOpDir(flinkIOFile);
                String opDir = flinkIOFile.getPath() + "/" + opDirName;
                result.put(new File(opDir), opDirName);
            }
        }
        return result;
    }

    private static String getOpDir(File flinkIOFile) {
        File[] opDirs = flinkIOFile.listFiles();
        if (opDirs == null || opDirs.length == 0) {
            return BLANK_DIR;
        }
        return opDirs[0].getName();
    }
}
