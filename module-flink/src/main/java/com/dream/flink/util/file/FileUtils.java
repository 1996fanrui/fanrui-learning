package com.dream.flink.util.file;

import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.local.LocalFileSystem;

import java.io.IOException;

public class FileUtils {

    public static void deleteRecursive(String path) throws IOException {
        LocalFileSystem fs = new LocalFileSystem();
        fs.delete(new Path(path), true);
    }

    public static void main(String[] args) throws IOException {
        deleteRecursive("/tmp/used_dir");
    }

}
