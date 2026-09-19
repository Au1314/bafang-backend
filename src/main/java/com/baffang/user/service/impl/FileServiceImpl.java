package com.baffang.user.service.impl;

import com.baffang.common.enums.ErrorMsg;
import com.baffang.common.exception.BusinessException;
import com.baffang.common.util.FileUtil;
import com.baffang.user.dto.FileUploadDTO;
import com.baffang.user.service.FileService;
import com.baffang.user.vo.FileUploadResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传下载服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 上传文件
     *
     * @param fileUploadDTO 文件上传请求DTO
     * @return 上传成功后的文件信息，包含文件名和下载路径
     */
    @Override
    public FileUploadResultVO uploadFile(FileUploadDTO fileUploadDTO) {
        // 验证文件是否为空
        MultipartFile file = fileUploadDTO.getFile();
        if (ObjectUtils.isEmpty(file) || file.isEmpty()) {
            throw new BusinessException(ErrorMsg.UPLOAD_FAILED);
        }

        // 确保上传目录存在
        if (!FileUtil.ensureDirectoryExists(uploadPath)) {
            throw new BusinessException(ErrorMsg.UPLOAD_FAILED);
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = FileUtil.generateUniqueFilename(originalFilename);

            // 创建目标文件
            File dest = new File(uploadPath, uniqueFilename);

            // 保存文件
            file.transferTo(dest);

            // 构造文件下载URL
            String downloadUrl = "/api/v1/user/file/download/" + uniqueFilename;

            // 构造返回结果
            FileUploadResultVO resultVO = new FileUploadResultVO();
            resultVO.setFileName(uniqueFilename);
            resultVO.setUrl(downloadUrl);

            return resultVO;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new BusinessException(ErrorMsg.UPLOAD_FAILED);
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     * @param response HttpServletResponse
     * @throws IOException 异常
     */
    @Override
    public void downloadFile(String fileName, HttpServletResponse response) throws IOException {
        // 构造文件路径
        File file = new File(uploadPath, fileName);

        // 检查文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new BusinessException(ErrorMsg.FILE_NOT_FOUND);
        }

        // 设置响应头
        response.setContentType(FileUtil.getMimeType(fileName));
        response.setContentLengthLong(file.length());
        response.setHeader("Content-Disposition",
                "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 读取文件并写入响应流
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage());
            throw new BusinessException(ErrorMsg.DOWNLOAD_FAILED);
        }
    }
}