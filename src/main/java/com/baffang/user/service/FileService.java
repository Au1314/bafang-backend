package com.baffang.user.service;

import com.baffang.user.dto.FileUploadDTO;
import com.baffang.user.vo.FileUploadResultVO;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件上传下载服务接口
 */
public interface FileService {
    /**
     * 上传文件
     *
     * @param fileUploadDTO 文件上传请求DTO
     * @return 上传成功后的文件信息，包含文件名和下载路径
     */
    FileUploadResultVO uploadFile(FileUploadDTO fileUploadDTO);

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     * @param response HttpServletResponse
     * @throws IOException 异常
     */
    void downloadFile(String fileName, HttpServletResponse response) throws IOException;
}