package com.dream.flink.io.jmh;

import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.CheckpointStreamFactory;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * @author fanrui03
 * @date 2020/10/31 13:04
 */
public class InputBenchmark extends IOBenchmarkBase {

    private StreamStateHandle stateHandle;
    DataInputViewStreamWrapper inputView;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + InputBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

    @Override
    void doSetup() throws IOException {
        initFile();
        // outputStream of checkpoint
        CheckpointStreamFactory.CheckpointStateOutputStream outputStream =
                new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(
                        filePath, filePath.getFileSystem(), MB, MB);

        DataOutputViewStreamWrapper outView = new DataOutputViewStreamWrapper(outputStream);
        outView.writeInt(bytesCount);

        byte[] bytes = new byte[bytesLength];
        for (int i = 0; i < bytesCount; i++) {
            outView.write(bytes);
        }
        stateHandle = outputStream.closeAndGetHandle();
    }

    @Override
    void doSetupPerIteration() throws IOException {
        FSDataInputStream fsDataInputStream = stateHandle.openInputStream();
        if (bufferSize == 0) {
            inputView = new DataInputViewStreamWrapper(fsDataInputStream);
            return;
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fsDataInputStream, bufferSize);
        inputView = new DataInputViewStreamWrapper(bufferedInputStream);
    }

    @Benchmark
    public void read() throws IOException {
        int readBytesCount = inputView.readInt();
        byte[] bytes = new byte[bytesLength];
        for (int i = 0; i < readBytesCount; i++) {
            inputView.readFully(bytes);
        }
    }

    @Override
    void doTearDownPerInvocation() throws Exception {
        inputView.close();
    }

    @Override
    void doTearDown() throws Exception {
        stateHandle.discardState();
        file.delete();
    }
}
