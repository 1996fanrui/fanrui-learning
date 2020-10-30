package com.dream.flink.io.jmh;

import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.CheckpointStreamFactory;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FileBasedStateOutputStream;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.io.IOException;

/**
 * @author fanrui03
 * @date 2020/10/31 11:12
 */
public class OutputBenchmark extends IOBenchmarkBase {

    private CheckpointStreamFactory.CheckpointStateOutputStream outputStream;
    private DataOutputViewStreamWrapper outView;
    private StreamStateHandle stateHandle;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .verbosity(VerboseMode.NORMAL)
                .include(".*" + OutputBenchmark.class.getSimpleName() + ".*")
                .build();

        new Runner(opt).run();
    }

    @Override
    void doSetup() {
    }

    @Override
    void doSetupPerIteration() throws IOException {
        initFile();
        initOutputStream();
    }

    private void initOutputStream() throws IOException {
        if (bufferSize == 0) {
            // outputStream of Local recovery
            outputStream = new FileBasedStateOutputStream(filePath.getFileSystem(), filePath);
        } else {
            // outputStream of checkpoint
            outputStream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(
                    filePath, filePath.getFileSystem(), bufferSize, bufferSize);
        }

        outView = new DataOutputViewStreamWrapper(outputStream);
        outView.writeInt(bytesCount);
    }

    @Benchmark
    public void write() throws IOException {
        byte[] bytes = new byte[bytesLength];
        for (int i = 0; i < bytesCount; i++) {
            outView.write(bytes);
        }
        stateHandle = outputStream.closeAndGetHandle();
    }

    @Override
    void doTearDownPerInvocation() throws Exception {
        stateHandle.discardState();
        if (file == null) {
            return;
        }
        boolean delete = file.delete();
        if (delete) {
            return;
        }
        file.deleteOnExit();
    }

    @Override
    void doTearDown() throws IOException {
    }
}
