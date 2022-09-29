package com.dream.flink.state.backend;

import org.rocksdb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fanrui03
 * @time 2020-05-24 14:49:45
 */
public class RocksDBCompactionDemo {

    private static String ROCKSDB_DIR = "/tmp/test/rocksdb/";

    public static void main(String[] args) throws RocksDBException, InterruptedException {

        List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>(1);
        columnFamilyDescriptors.add(
                new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, createColumnOptions()));
        RocksDB db = RocksDB.open(createDBOptions(), ROCKSDB_DIR + System.currentTimeMillis(), columnFamilyDescriptors, new ArrayList<>());

        db.compactRange();
        int count = 20000000;
//        count = 5000;
        for (int i = 0; i < count; i++) {
            db.put((i + "keyjskdf").getBytes(), (i + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf" + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf" + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf").getBytes());
            if (i % 1000000 == 0) {
                System.out.println(i);
            }
        }

        System.out.println("write done");
        System.out.println("query : " + new String(db.get((1 + "keyjskdf").getBytes())));


        int sleep = 10;
//        TimeUnit.SECONDS.sleep(sleep);
//        System.out.println("start compact");
//
        long startTime = System.currentTimeMillis();
//        db.compactRange();

        System.out.println(" compact done" + (System.currentTimeMillis() - startTime));

//        System.out.println(new String(db.get(KEY)));
//        TimeUnit.SECONDS.sleep(200);

        Checkpoint checkpoint = Checkpoint.create(db);
        String checkpointPath = ROCKSDB_DIR + "checkpoint/" + System.currentTimeMillis();
        System.out.println("checkpointPath:" + checkpointPath);
        checkpoint.createCheckpoint(checkpointPath);

        db = RocksDB.open(createDBOptions(), checkpointPath, columnFamilyDescriptors, new ArrayList<>());

        System.out.println("restore : " + new String(db.get((1 + "keyjskdf").getBytes())));



        startTime = System.currentTimeMillis();
        db.compactRange();

        System.out.println(" compact done" + (System.currentTimeMillis() - startTime));

//        System.out.println(new String(db.get(KEY)));
        TimeUnit.SECONDS.sleep(sleep * 3);

//        for (int i = 0; i < count; i++) {
//            db.put(((i + count) + "keyjskdf").getBytes(), (i + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf" + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf" + "valuesjdofsldjflsdfjsldjsljflsdfklsjldfjlsdjfljsklfjsljflsjlflsnnksnvknknf").getBytes());
//            if (i % 1000000 == 0) {
//                System.out.println(i);
//            }
//        }
//
//        System.out.println("write done");
//
//        TimeUnit.SECONDS.sleep(sleep);
//        System.out.println("start compact");
//
//        startTime = System.currentTimeMillis();
//        db.compactRange();
//
//        System.out.println(" compact done" + (System.currentTimeMillis() - startTime));
//
////        System.out.println(new String(db.get(KEY)));
//        TimeUnit.SECONDS.sleep(sleep * 3);
    }

    public static DBOptions createDBOptions() {
        DBOptions dbOptions = new DBOptions()
                .setIncreaseParallelism(8)
                .setUseFsync(false)
                .setCreateIfMissing(true)
                .setMaxOpenFiles(-1)
                .setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL)
                .setStatsDumpPeriodSec(0);

        dbOptions.setMaxSubcompactions(4);
        return dbOptions;
    }

    public static ColumnFamilyOptions createColumnOptions() {

        final long blockSize = 4 * 1024;
        final long targetFileSize = 64 * 1024 * 1024;
        final long writeBufferSize = 64 * 1024 * 1024;

        BloomFilter bloomFilter = new BloomFilter();

        return new ColumnFamilyOptions()
                .setCompactionStyle(CompactionStyle.LEVEL)
                .setLevelCompactionDynamicLevelBytes(true)
                .setTargetFileSizeBase(targetFileSize)
                .setMaxBytesForLevelBase(4 * targetFileSize)
                .setMaxBytesForLevelMultiplier(8)
                .setWriteBufferSize(writeBufferSize)
                .setMinWriteBufferNumberToMerge(1)
                .setMaxWriteBufferNumber(3)
                .setTableFormatConfig(
                        new BlockBasedTableConfig()
                                .setBlockSize(blockSize)
                                .setFilter(bloomFilter));
    }

}
