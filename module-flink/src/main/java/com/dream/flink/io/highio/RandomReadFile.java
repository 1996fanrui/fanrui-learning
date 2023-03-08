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
import java.util.concurrent.atomic.AtomicLong;

/**
 * java -cp 0a3a4be6513d489db38c4e02d459a37a com.dream.flink.io.highio.RandomReadFile --threadCount 100 --workDir /mnt/disk/10/yarn/test --fileLengthMB 1000 --durationMinutes 10
 * java -cp 55f8f8f2479240649e63d9364baf3253 com.dream.flink.io.highio.RandomReadFile --threadCount 100 --workDir /mnt/disk/10/yarn/test --fileLengthMB 1000 --durationMinutes 10
 *
 *
 * 顺序写：
 * fio --randrepeat=1 --ioengine=libaio --direct=1 --gtod_reduce=1 --name=rwtest --filename=/mnt/disk/10/yarn/test/rwtest1 --rwmixread=70 --bs=4k --iodepth=64 --size=300G --readwrite=write  --max-jobs=20
 *
 */
public class RandomReadFile {

    private static final int DEFAULT_THREAD_COUNT = 10;
    private static final String DEFAULT_WORK_DIR = "/tmp/random_read_file";
    private static final int DEFAULT_FILE_LENGTH_MB = 100;
    private static final int DEFAULT_DURATION_MINUTES = 2;

    public static void main(String[] args) throws InterruptedException, IOException {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        int threadCount = parameterTool.getInt("threadCount", DEFAULT_THREAD_COUNT);
        String workDir = parameterTool.get("workDir", DEFAULT_WORK_DIR);
        int fileLength = parameterTool.getInt("fileLengthMB", DEFAULT_FILE_LENGTH_MB) * 1024 * 1024;
        int durationMinutes = parameterTool.getInt("durationMinutes", DEFAULT_DURATION_MINUTES);

        AtomicLong counter = new AtomicLong(0);

        Path path = new Path(workDir, UUID.randomUUID().toString());

        initializeFile(path, fileLength);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(new RandomReadTask(path, fileLength, counter));
        }

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() ->
                System.out.println(counter.getAndSet(0)),0, 1, TimeUnit.SECONDS);

        TimeUnit.MINUTES.sleep(durationMinutes);
        executor.shutdownNow();

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
        private final AtomicLong counter;

        private RandomReadTask(Path filePath, int fileLength, AtomicLong counter) {
            this.filePath = filePath;
            this.fileLength = fileLength;
            this.counter = counter;
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
                    counter.incrementAndGet();
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
