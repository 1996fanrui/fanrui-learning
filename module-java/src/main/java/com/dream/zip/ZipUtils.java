package com.dream.zip;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ZipUtils {

    public static void packageZip(File unzippedFile, String zippedFilePath) throws Exception {
        validationIsNull(unzippedFile, zippedFilePath);
        new ZipFile(zippedFilePath).addFile(unzippedFile);
    }

    public static void packageZip(File unzippedFile, String zippedFilePath, String password) throws Exception {
        validationIsNull(unzippedFile, zippedFilePath, password);
        ZipFile zipFile = new ZipFile(zippedFilePath, password.toCharArray());
        zipFile.addFile(unzippedFile, getZipParameters());
    }

    public static void packageZip(List<File> unzippedFileList, String zippedFilePath) throws Exception {
        validationIsNull(unzippedFileList, zippedFilePath);
        new ZipFile(zippedFilePath).addFiles(unzippedFileList);
    }

    public static void packageZip(List<File> unzippedFileList, String zippedFilePath, String password) throws Exception {
        validationIsNull(unzippedFileList, zippedFilePath, password);
        ZipFile zipFile = new ZipFile(zippedFilePath, password.toCharArray());
        zipFile.addFiles(unzippedFileList, getZipParameters());
    }

    public static void packageFolderToZip(String catalogPath, String zippedFilePath) throws Exception {
        validationIsNull(catalogPath, zippedFilePath);
        new ZipFile(zippedFilePath).addFolder(new File(catalogPath));
    }

    public static void packageFolderToZip(String unzippedFolder, String zippedFilePath, String password) throws Exception {
        validationIsNull(unzippedFolder, zippedFilePath, password);
        ZipFile zipFile = new ZipFile(zippedFilePath, password.toCharArray());
        zipFile.addFolder(new File(unzippedFolder), getZipParameters());
    }

    public static void unzipToFolder(String zippedFilePath, String unzippedFolder) throws Exception {
        validationIsNull(zippedFilePath, unzippedFolder);
        new ZipFile(zippedFilePath).extractAll(unzippedFolder);
    }

    public static void unzipToFolder(String zippedFilePath, String unzippedFolder, String password) throws Exception {
        validationIsNull(zippedFilePath, unzippedFolder);
        new ZipFile(zippedFilePath, password.toCharArray()).extractAll(unzippedFolder);
    }

    /**
     * 解压指定的文件
     *
     * @param zippedFilePath 待解压的压缩包绝对路径
     * @param targetFilePath 目标文件相对目录，基于压缩包根目录
     * @param unzipCatalog   解压后的目录
     * @throws Exception
     *
     * @author by SPACE
     * @log create on 2020年6月15日下午3:56:15
     */
    public static void unzipTargetFile(String zippedFilePath, String targetFilePath, String unzipCatalog)
            throws Exception {
        new ZipFile(zippedFilePath).extractFile(targetFilePath, unzipCatalog);
    }

    /**
     * 从设置了密码的压缩包中解压指定的文件
     *
     * @param zippedFilePath    待解压的压缩包绝对路径
     * @param targetFilePath 目标文件相对目录，基于压缩包根目录，
     *              <span style="color:red">例如 msg/success/msg.txt</span>
     * @param unzipCatalog   解压后的目录
     * @param password       压缩包密码
     */
    public static void unzipTargetFile(String zippedFilePath, String targetFilePath, String unzipCatalog, String password)
            throws Exception {
        new ZipFile(zippedFilePath, password.toCharArray()).extractFile(targetFilePath, unzipCatalog);
    }

    static void validationIsNull(Object... objects) throws NullPointerException {
        for (Object object : objects) {
            if (Objects.isNull(object)) {
                throw new NullPointerException("param is null");
            }
        }
    }

    static ZipParameters getZipParameters() {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        return zipParameters;
    }

    public static void main(String[] args) throws Exception {
        String password = UUID.randomUUID().toString() + UUID.randomUUID();
        System.out.println(password);
        File unzippedFile = new File("/Users/fanrui/Documents/backup/mvn-1.log");
        packageZip(unzippedFile, "/Users/fanrui/Documents/backup/sjkfjkfsljdfsjfslj", password);
    }

}
