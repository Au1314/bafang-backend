package com.baffang.user.dto;

import lombok.Data;

/**
 * 发起人工客服咨询请求DTO
 */
@Data
public class ManualServiceConsultationDTO {
    /**
     * 咨询内容
     */
    private String content;
}