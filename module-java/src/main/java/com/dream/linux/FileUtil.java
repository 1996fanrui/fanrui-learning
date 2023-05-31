package com.dream.linux;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {

    public static void writeWithAppend(File file, byte[] data) throws IOException {
        write(file, file.length(), data);
    }

    public static void write(File file, long pos, byte[] data) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(pos);
        randomAccessFile.write(data);
        randomAccessFile.close();
    }

    public static void read(File file, long pos, byte[] data) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(pos);
        randomAccessFile.read(data);
        randomAccessFile.close();
    }

}
