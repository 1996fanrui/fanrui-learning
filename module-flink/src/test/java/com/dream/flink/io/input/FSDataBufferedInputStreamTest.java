package com.dream.flink.io.input;

import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.memory.DataInputViewStreamWrapper;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author fanrui03
 * @date 2020/10/31 20:37
 */
public class FSDataBufferedInputStreamTest {

    private static final int KB = 1024;
    private static final int MB = 1024 * KB;

    FsCheckpointStreamFactory.FsCheckpointStateOutputStream outputStream;
    DataOutputViewStreamWrapper outView;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        String path = temporaryFolder.newFolder().getAbsolutePath();
        File outFile = new File(path, String.valueOf(UUID.randomUUID()));

        Path outPath = new Path(outFile.toURI());
        outputStream = new FsCheckpointStreamFactory.FsCheckpointStateOutputStream(
                outPath, outPath.getFileSystem(), MB, KB);
        outView = new DataOutputViewStreamWrapper(outputStream);
    }

    @Test
    public void testOrderRead() throws Exception {
        // write
        int num = 10_000_000;
        StreamStateHandle stateHandle = writeData(num);
        Assert.assertNotNull(stateHandle);

        int expectedStateSize = 2 * num * Integer.BYTES;
        Assert.assertEquals(expectedStateSize, stateHandle.getStateSize());

        // start read
        FSDataInputStream fsDataInputStream = stateHandle.openInputStream();
        FSDataBufferedInputStream bufferedInputStream = new FSDataBufferedInputStream(fsDataInputStream);
        DataInputViewStreamWrapper inputView = new DataInputViewStreamWrapper(bufferedInputStream);

        for (int i = 1; i <= num; i++) {
            int read = inputView.readInt();
            Assert.assertEquals(i, read);
        }
        for (int i = num; i >= 1; i--) {
            int read = inputView.readInt();
            Assert.assertEquals(i, read);
        }
        bufferedInputStream.close();
        stateHandle.discardState();
    }

    @Test
    public void testSeek() throws Exception {
        // write
        int num = 1_000_000;
        StreamStateHandle stateHandle = writeData(num);
        Assert.assertNotNull(stateHandle);

        int expectedStateSize = 2 * num * Integer.BYTES;
        Assert.assertEquals(expectedStateSize, stateHandle.getStateSize());

        // create inputStream
        FSDataInputStream fsDataInputStream = stateHandle.openInputStream();
        FSDataBufferedInputStream bufferedInputStream = new FSDataBufferedInputStream(fsDataInputStream);
        DataInputViewStreamWrapper inputView = new DataInputViewStreamWrapper(bufferedInputStream);

        Assert.assertEquals(expectedStateSize, bufferedInputStream.available());

        for (int j = 0; j < 3; j++) {
            checkSeekPos(num, expectedStateSize, bufferedInputStream, inputView);
        }

        bufferedInputStream.close();
        stateHandle.discardState();
    }

    private void checkSeekPos(
            int num,
            int expectedStateSize,
            FSDataBufferedInputStream bufferedInputStream,
            DataInputViewStreamWrapper inputView) throws IOException {
        int offset = 0;
        for (int i = 1; i <= num; i++) {
            // test for pos and available
            bufferedInputStream.seek(offset);
            Assert.assertEquals(offset, bufferedInputStream.getPos());
            int avail = expectedStateSize - offset;
            Assert.assertEquals(avail, bufferedInputStream.available());

            // test for data
            int read = inputView.readInt();
            Assert.assertEquals(i, read);
            offset += Integer.BYTES;
        }
        for (int i = num; i >= 1; i--) {
            // test for pos and available
            bufferedInputStream.seek(offset);
            Assert.assertEquals(offset, bufferedInputStream.getPos());
            int avail = expectedStateSize - offset;
            Assert.assertEquals(avail, bufferedInputStream.available());

            // test for data
            int read = inputView.readInt();
            Assert.assertEquals(i, read);
            offset += Integer.BYTES;
        }
    }

    private StreamStateHandle writeData(int num) throws IOException {
        for (int i = 1; i <= num; i++) {
            outView.writeInt(i);
        }
        for (int i = num; i >= 1; i--) {
            outView.writeInt(i);
        }
        return outputStream.closeAndGetHandle();
    }

}
