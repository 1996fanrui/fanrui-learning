package com.dream.flink.io;

import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @date 2020/10/30 20:16
 */
public class IOSimpleBenchmark {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;

    private static final int BYTES_LENGTH = 100;

    private static final int FILE_SIZE = 500 * MB;

    private static final int BYTES_COUNT = FILE_SIZE / BYTES_LENGTH;

    public static void main(String[] args) throws Exception {

        File outFile = new File("module-flink/.tmp", String.valueOf(UUID.randomUUID()));
        System.out.println(outFile.getPath());
        long startTime = System.currentTimeMillis();

        // write
        Path outPath = new Path(outFile.toURI());
        FsCheckpointStreamFactory.FsCheckpointStateOutputStream outputStream =
                new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(
                        outPath, outPath.getFileSystem(), MB, KB);
        outFile.deleteOnExit();

        final DataOutputViewStreamWrapper outView = new DataOutputViewStreamWrapper(outputStream);
        outView.writeInt(BYTES_COUNT);
        byte[] bytes = new byte[BYTES_LENGTH];
        for (int i = 0; i < BYTES_COUNT; i++) {
            outView.write(bytes);
        }
        StreamStateHandle stateHandle = outputStream.closeAndGetHandle();
        long writeTime = System.currentTimeMillis();
        long writeDuration = writeTime - startTime;
        int fileSize = outView.size() / MB;
        System.out.println("write duration : " + writeDuration + " ms, size:" + fileSize + "MB");

        if (stateHandle == null) {
            System.out.println("StreamStateHandle 为 null");
        }

        // start read
        FSDataInputStream fsDataInputStream = stateHandle.openInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fsDataInputStream);
        DataInputViewStreamWrapper inputView = new DataInputViewStreamWrapper(bufferedInputStream);
        int bytesCount = inputView.readInt();
        System.out.println("read bytesCount：" + bytesCount);
        for (int i = 0; i < bytesCount; i++) {
            inputView.readFully(bytes);
        }
        long readTime = System.currentTimeMillis();
        long readDuration = readTime - writeTime;
        System.out.println("readDuration : " + readDuration + " ms");

        stateHandle.discardState();
    }

    static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ignored) {
        }
    }

}
