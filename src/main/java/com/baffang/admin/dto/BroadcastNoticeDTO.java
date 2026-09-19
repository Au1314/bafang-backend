package com.baffang.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 群发公告请求 DTO
 */
@Data
public class BroadcastNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "通知标题不能为空")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    private String content;
}