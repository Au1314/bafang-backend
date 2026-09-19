package com.baffang.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送通知请求 DTO
 */
@Data
public class SendNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 接收用户ID（为空则发送给全体用户） */
    private Integer userId;

    @NotBlank(message = "通知标题不能为空")
    private String title;

    @NotBlank(message = "通知内容不能为空")
    private String content;

    @NotNull(message = "通知类型不能为空")
    private Integer type;

    /** 关联业务ID（可选） */
    private Integer relatedId;

    /** 关联业务类型（可选） */
    private Integer relatedType;
}