package com.baffang.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件处理工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + fileSuffix;
    }

    /**
     * 确保目录存在，如果不存在则创建
     *
     * @param directoryPath 目录路径
     * @return 是否创建成功
     */
    public static boolean ensureDirectoryExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (IOException e) {
                log.error("创建目录失败: {}", e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件的MIME类型
     *
     * @param fileName 文件名
     * @return MIME类型
     */
    public static String getMimeType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }
        String fileSuffix = fileName.toLowerCase().substring(fileName.lastIndexOf(".") + 1);
        return switch (fileSuffix) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            case "ppt", "pptx" -> "application/vnd.ms-powerpoint";
            case "txt" -> "text/plain";
            case "html" -> "text/html";
            case "xml" -> "text/xml";
            case "json" -> "application/json";
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            default -> "application/octet-stream";
        };
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param destFile   目标文件
     * @return 是否复制成功
     */
    public static boolean copyFile(File sourceFile, File destFile) {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            log.error("复制文件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.length();
        }
        return 0;
    }
}