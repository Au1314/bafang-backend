package com.baffang.user.controller;

import com.baffang.common.result.Result;
import com.baffang.user.dto.FileUploadDTO;
import com.baffang.user.service.FileService;
import com.baffang.user.vo.FileUploadResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件上传下载控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/user")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传接口
     *
     * @param file     上传的文件
     * @param fileType 文件类型（可选）
     * @return 上传结果（包含文件名和下载URL）
     */
    @Operation(summary = "文件上传")
    @PostMapping("/file/upload")
    public Result<FileUploadResultVO> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", required = false) String fileType) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(file);
        fileUploadDTO.setFileType(fileType);
        FileUploadResultVO resultVO = fileService.uploadFile(fileUploadDTO);
        return Result.success(resultVO);
    }

    /**
     * 文件下载接口
     *
     * @param fileName 文件名
     * @param response HttpServletResponse
     * @throws IOException 异常
     */
    @Operation(summary = "文件下载")
    @GetMapping("/file/download/{fileName}")
    public void downloadFile(@PathVariable("fileName") String fileName, HttpServletResponse response)
            throws IOException {
        fileService.downloadFile(fileName, response);
    }
}