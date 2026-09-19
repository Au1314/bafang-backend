package com.baffang.user.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求DTO
 */
@Data
public class FileUploadDTO {
    /**
     * 上传的文件
     */
    private MultipartFile file;
    
    /**
     * 文件类型（可选，用于分类存储）
     */
    private String fileType;
}