package com.dream.flink.io.highio;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * nohup java -cp 5667b3e04d86495aa5502c128c9a79e8 com.dream.flink.io.highio.HighCPUAndIO --threadCount 30 --durationMinutes 10 --workDir /mnt/disk/10/yarn/test --fileLengthMB 1000 --ioThreadCount 2 &
 * <p>
 * 验证有一个线程访问 io 的场景，当前进程是否会影响？
 */
public class HighCPUAndIO {

    private static final int DEFAULT_THREAD_COUNT = 5;
    private static final int DEFAULT_DURATION_MINUTES = 2;
    private static final String DEFAULT_WORK_DIR = "/tmp/random_read_file";
    private static final int DEFAULT_FILE_LENGTH_MB = 100;

    public static void main(String[] args) throws InterruptedException, IOException {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int threadCount = parameterTool.getInt("threadCount", DEFAULT_THREAD_COUNT);
        int durationMinutes = parameterTool.getInt("durationMinutes", DEFAULT_DURATION_MINUTES);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new CPUTask());
        }

        int ioThreadCount = parameterTool.getInt("ioThreadCount", DEFAULT_THREAD_COUNT);
        String workDir = parameterTool.get("workDir", DEFAULT_WORK_DIR);
        int fileLength = parameterTool.getInt("fileLengthMB", DEFAULT_FILE_LENGTH_MB) * 1024 * 1024;

        Path path = new Path(workDir, UUID.randomUUID().toString());
        initializeFile(path, fileLength);
        ExecutorService ioExecutor = Executors.newFixedThreadPool(ioThreadCount);
        for (int i = 0; i < ioThreadCount; i++) {
            executor.submit(new RandomReadTask(path, fileLength));
        }

        TimeUnit.MINUTES.sleep(durationMinutes);
        executor.shutdownNow();
        ioExecutor.shutdownNow();

        // clean up the work dir.
    }

    private static void initializeFile(Path filePath, int fileLength) throws IOException {
        FileSystem fileSystem = filePath.getFileSystem();
        FSDataOutputStream outputStream = fileSystem.create(filePath, FileSystem.WriteMode.OVERWRITE);
        byte[] data = new byte[fileLength];
        outputStream.write(data);
        outputStream.flush();
    }

    private static class RandomReadTask implements Runnable {

        private final Path filePath;
        private final int fileLength;

        private RandomReadTask(Path filePath, int fileLength) {
            this.filePath = filePath;
            this.fileLength = fileLength;
        }

        @Override
        public void run() {
            try {
                FileSystem fileSystem = filePath.getFileSystem();
                FSDataInputStream inputStream = fileSystem.open(filePath);
                Random random = new Random();
                byte[] readData = new byte[1];
                while (true) {
                    int offset = random.nextInt(fileLength - 10);
                    inputStream.seek(offset);
                    inputStream.read(readData);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static class CPUTask implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
