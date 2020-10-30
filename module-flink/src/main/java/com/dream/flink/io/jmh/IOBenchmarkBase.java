package com.dream.flink.io.jmh;

import org.apache.flink.core.fs.Path;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author fanrui03
 * @date 2020/10/31 10:59
 */
public abstract class IOBenchmarkBase extends BenchmarkBase {

    protected static final int KB = 1024;
    protected static final int MB = 1024 * KB;

    private static final int FILE_SIZE = 500 * MB;

    @Param({"100", "1000"})
    int bytesLength;

    @Param({"0", "1000", "10000", "1000000"})   // no buffer, 1KB, 10KB, 1MB
    int bufferSize;

    protected int bytesCount;

    protected File file;

    protected Path filePath;

    protected void initFile(){
        file = new File("module-flink/.tmp", String.valueOf(UUID.randomUUID()));
        System.out.println(file.getPath());
        filePath = new Path(file.toURI());
    }

    @Setup
    public void setup() throws IOException {
        bytesCount = FILE_SIZE / bytesLength;
        doSetup();
    }

    abstract void doSetup() throws IOException;

    @TearDown
    public void tearDown() throws Exception {
        doTearDown();
    }

    abstract void doTearDown() throws Exception;

    @Setup(Level.Invocation)
    public void setUpPerIteration() throws IOException {
        doSetupPerIteration();
    }

    abstract void doSetupPerIteration() throws IOException;

    @TearDown(Level.Invocation)
    public void tearDownPerInvocation() throws Exception {
        doTearDownPerInvocation();
    }

    abstract void doTearDownPerInvocation() throws Exception;

}
