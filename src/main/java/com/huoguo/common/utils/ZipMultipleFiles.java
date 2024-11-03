package com.huoguo.common.utils;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMultipleFiles {

    @SneakyThrows
    public static void zipFiles(List<String> filesToZip, String outputZipFile) {
        FileOutputStream fos = new FileOutputStream(outputZipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        byte[] bytes = new byte[1024];
        for (String file : filesToZip) {
            try (FileInputStream fis = new FileInputStream(file)) {
                // 新建一个ZipEntry，并把它加入到ZipOutputStream中
                ZipEntry zipEntry = new ZipEntry(file.substring(file.lastIndexOf('/') + 1));
                zos.putNextEntry(zipEntry);

                // 读取文件并写入到ZipOutputStream中
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                zos.closeEntry(); // 关闭当前的ZipEntry
            }
        }
    }
}