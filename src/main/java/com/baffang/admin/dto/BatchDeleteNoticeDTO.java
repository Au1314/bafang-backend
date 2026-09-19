package com.baffang.admin.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量删除请求 DTO
 */
@Data
public class BatchDeleteNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "通知ID列表不能为空")
    private List<Integer> ids;
}
