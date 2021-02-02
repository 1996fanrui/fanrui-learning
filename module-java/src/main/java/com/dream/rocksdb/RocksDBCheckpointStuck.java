package com.dream.rocksdb;

import org.rocksdb.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @date 2021/2/2 18:17
 */
public class RocksDBCheckpointStuck {

    private static String ROOT_DIR = "./tmp/rocksdb/";

    public static void main(String[] args) throws RocksDBException, IOException, InterruptedException {
        String rocksDir = ROOT_DIR + System.currentTimeMillis();
        String dbPath = rocksDir + "/db";
        String chkDir = rocksDir + "/chk";
        System.out.println("dbPath:" + dbPath);
        RocksDB db = openDB(dbPath, getBadCaseColumnFamilyOptions());

        int[] batchs = new int[]{200000, 90000, 40000, 18000};

        int startIndex = 0;
        for (int i = 0; i < batchs.length; i++) {
            startIndex = write(db, startIndex, batchs[i]);
            db.flush(new FlushOptions().setWaitForFlush(true));
        }

        write(db, startIndex, 2_000_000);

        TimeUnit.SECONDS.sleep(10);

        System.out.println("start checkpoint.");
        Checkpoint checkpoint = Checkpoint.create(db);
        checkpoint.createCheckpoint(chkDir);

        System.out.println("end.");
    }

    private static int write(RocksDB db, int startIndex, int batch) throws RocksDBException {
        int endIndex = startIndex + batch;
        for (int i = startIndex; i < endIndex; i++) {
            String key = i + "key_key_key";
            String value = i + "value_value_value";
            db.put(key.getBytes(), value.getBytes());
        }
        return endIndex;
    }

    private static RocksDB openDB(
            String path,
            ColumnFamilyOptions columnFamilyOptions) throws IOException, RocksDBException {
        if (!new File(path).mkdirs()) {
            throw new IOException(String.format(
                    "Could not create RocksDB data directory at %s.",
                    path));
        }
        DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL);

        List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>(1);
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, columnFamilyOptions));

        return RocksDB.open(dbOptions, path, columnFamilyDescriptors, new ArrayList<>(1));
    }

    private static ColumnFamilyOptions getBadCaseColumnFamilyOptions() {
        return new ColumnFamilyOptions()
                .setCompactionStyle(CompactionStyle.UNIVERSAL)
                .setMaxWriteBufferNumber(4)
                .setMinWriteBufferNumberToMerge(3);
    }


}
