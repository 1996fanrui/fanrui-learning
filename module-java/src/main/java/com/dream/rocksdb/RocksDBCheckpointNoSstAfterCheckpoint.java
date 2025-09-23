package com.dream.rocksdb;

import org.rocksdb.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Reproduce all sst files are deleted after the rocksdb is restored from checkpoint.
 * There are 4 sst files in rocksdb, all records are deletion markers.
 * The 4th sst files are flushed by flush, that is why the previous rocksdb does not compact them.
 */
public class RocksDBCheckpointNoSstAfterCheckpoint {

    private static String ROOT_DIR = "./tmp/rocksdb/";

    public static void main(String[] args) throws RocksDBException, IOException, InterruptedException {
        String rocksDir = ROOT_DIR + System.currentTimeMillis();
        String dbPath = rocksDir + "/db";
        String chkDir = rocksDir + "/chk";
        String exportDir = rocksDir + "/export";
        System.out.println("dbPath:" + dbPath);
        ColumnFamilyOptions badCaseColumnFamilyOptions = new ColumnFamilyOptions();
        ArrayList<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>(1);
        RocksDB db = openDB(dbPath, badCaseColumnFamilyOptions, columnFamilyHandles);

        int globalIndex = 10;
        for (int i = 0; i < 4; i++) {
            db.flush(new FlushOptions().setWaitForFlush(true));
            for (int j = 0; j < 10; j++, globalIndex++) {
                // There is no key group range intersection between multiple sst files
                // Trigger trivial_move instead of physical compaction.
                // String key = i + "key_key_key";
                // There is key group range intersection between multiple sst files
                String key = j + "key_key_key";
                db.delete(key.getBytes());
            }
        }

        // 1. sstCountInDBPath after insert some deletion markers
        int sstCount = Objects.requireNonNull(Path.of(dbPath).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in DBPath : " + sstCount);

        // 2. Simulate checkpoint and restore
        Checkpoint checkpoint = Checkpoint.create(db);
        checkpoint.createCheckpoint(chkDir);
        sstCount = Objects.requireNonNull(Path.of(chkDir).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in checkpoint path: " + sstCount);
        checkpoint.close();
        db.close();


        sstCount = Objects.requireNonNull(Path.of(chkDir).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in checkpoint path after checkpoint is closed: " + sstCount);

        // 3. Restore from rocksdb checkpoint dir
        columnFamilyHandles = new ArrayList<>(1);

        // Highlight: Disable auto compaction for recovered rocksdb to prevent sst is deleted.
        badCaseColumnFamilyOptions.setDisableAutoCompactions(true);
        RocksDB recoveredDB = openDB(chkDir, badCaseColumnFamilyOptions, columnFamilyHandles);
        sstCount = Objects.requireNonNull(Path.of(chkDir).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in checkpoint path after new db is opened from checkpoint dir: " + sstCount);


        TimeUnit.SECONDS.sleep(1);
        // 4. sstCountInDBPath after insert some deletion markers
        sstCount = Objects.requireNonNull(Path.of(chkDir).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in new DBPath after 1 seconds: " + sstCount);

        System.out.println("start checkpoint.");
        Checkpoint secondCheckpoint = Checkpoint.create(recoveredDB);
        ExportImportFilesMetaData exportImportFilesMetaData =
                secondCheckpoint.exportColumnFamily(columnFamilyHandles.get(0), exportDir);

        // 5. sstCountInDBPath after insert some deletion markers
        sstCount = Objects.requireNonNull(Path.of(exportDir).toFile()
                .listFiles((file, name) -> name.toLowerCase().endsWith(".sst"))).length;
        System.out.println("sstCount in export path: " + sstCount);
        System.out.println("end.");
    }

    private static RocksDB openDB(
            String path,
            ColumnFamilyOptions columnFamilyOptions,
            ArrayList<ColumnFamilyHandle> columnFamilyHandles) throws RocksDBException {
        new File(path).mkdirs();
        DBOptions dbOptions = new DBOptions()
                .setCreateIfMissing(true)
                .setInfoLogLevel(InfoLogLevel.DEBUG_LEVEL);

        List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>(1);
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, columnFamilyOptions));

        return RocksDB.open(dbOptions, path, columnFamilyDescriptors, columnFamilyHandles);
    }

}
