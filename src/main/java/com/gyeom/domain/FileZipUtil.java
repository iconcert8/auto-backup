package com.gyeom.domain;

import com.gyeom.model.FileVO;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * file/directory zip util
 */
public class FileZipUtil {

    private FileZipUtil() {
    }

    private static final int BUFFER_SIZE = 4096;


    public static FileVO zip(List<FileVO> files) {
        List<File> localFiles = files.stream().map(f -> new File(f.getFullName())).collect(Collectors.toList());

        String zipDirectoryFullPath = AppConfiguration.getInstance().getTempZipFolder();
        String zipFileFullPath =
                zipDirectoryFullPath
                        + "/"
                        + DateTimeFormatter
                        .ofPattern(AppConfiguration.getInstance().getBackUpZipFileNamePattern())
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.now())
                        + ".zip";
        File zipDirectory = new File(zipDirectoryFullPath);
        File zipFile = new File(zipFileFullPath);

        try {
            if (!zipDirectory.exists()) {
                //noinspection ResultOfMethodCallIgnored
                zipDirectory.mkdir();
            }
            zip(localFiles, zipFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return FileVO.of(zipFile);
    }

    private static void zip(List<File> listFiles, String destZipFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        for (File file : listFiles) {
            if (!file.exists()) {
                continue;
            }
            if (file.isDirectory()) {
                zipDirectory(file, file.getName(), zos);
            } else {
                zipFile(file, zos);
            }
        }
        zos.flush();
        zos.close();
    }

    private static void zipDirectory(File folder, String parentFolder,
                                     ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            long bytesRead = 0;
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
                bytesRead += read;
            }
            zos.closeEntry();
        }
    }

    private static void zipFile(File file, ZipOutputStream zos)
            throws IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        long bytesRead = 0;
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
            bytesRead += read;
        }
        zos.closeEntry();
    }
}
