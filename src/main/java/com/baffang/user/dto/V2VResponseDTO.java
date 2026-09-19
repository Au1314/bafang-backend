package com.baffang.user.dto;

import lombok.Data;

/**
 * V2V充电呼叫响应请求DTO
 */
@Data
public class V2VResponseDTO {
    /**
     * 最终协商目标位置（接受时必填）
     * 必需
     * >= 1 字符
     * <= 200 字符
     */
    private String targetPosition;

    /**
     * 目标经度
     * 必需
     * >= -180
     * <= 180
     */
    private Double targetLongitude;

    /**
     * 目标纬度
     * 必需
     * >= -90
     * <= 90
     */
    private Double targetLatitude;
}