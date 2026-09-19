package com.baffang.user.vo;

import lombok.Data;

/**
 * 文件上传结果VO
 */
@Data
public class FileUploadResultVO {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件下载路径
     */
    private String url;
}