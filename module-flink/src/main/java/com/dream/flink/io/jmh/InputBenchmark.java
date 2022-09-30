package com.dream.flink.io.jmh;

import com.dream.flink.io.input.FSDataBufferedInputStream;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.CheckpointStateOutputStream;
import org.apache.flink.runtime.state.StreamStateHandle;
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
 * @date 2020/10/31 13:04
 *
 * nohup java -XX:+UseG1GC -Xmx10g -Xms10g -XX:MetaspaceSize=128m -XX:MaxGCPauseMillis=500 -XX:ParallelGCThreads=24 -XX:ConcGCThreads=6 -XX:+AggressiveOpts -XX:+DisableExplicitGC -XX:+ParallelRefProcEnabled -XX:-ResizePLAB -XX:+UseStringDeduplication -XX:+PrintAdaptiveSizePolicy -XX:InitiatingHeapOccupancyPercent=75 -XX:+UnlockExperimentalVMOptions -XX:G1HeapWastePercent=5 -XX:G1MixedGCLiveThresholdPercent=85 -XX:+PrintPromotionFailure -XX:MaxDirectMemorySize=2g -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -Dlog.file=./benchmark.log -Dlog4j.configuration=file:/home/hbase/fanrui/slimbase/log4j.properties -cp ./module-flink-1.0-SNAPSHOT-jar-with-dependencies.jar com.dream.flink.io.jmh.InputBenchmark > benchmark.out 2>&1 &
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
        CheckpointStateOutputStream outputStream =
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
        FSDataBufferedInputStream bufferedInputStream =
                new FSDataBufferedInputStream(fsDataInputStream, bufferSize);
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
