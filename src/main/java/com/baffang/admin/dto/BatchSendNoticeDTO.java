package com.baffang.admin.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量发送通知请求 DTO
 */
@Data
public class BatchSendNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "用户列表不能为空")
    private List<Integer> userIds;

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

