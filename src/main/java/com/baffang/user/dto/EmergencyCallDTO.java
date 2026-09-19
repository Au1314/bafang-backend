package com.baffang.user.dto;

import lombok.Data;

/**
 * 紧急救援呼叫请求DTO
 */
@Data
public class EmergencyCallDTO {
    /**
     * 呼叫类型：0=呼叫拖车，1=呼叫充电桩，2=其他呼叫
     * 必需
     */
    private Integer callType;

    /**
     * 呼叫范围（公里）
     * 必需
     */
    private Integer callRange;

    /**
     * 呼叫位置描述
     * 必需
     * >= 1 字符
     * <= 200 字符
     */
    private String callPosition;

    /**
     * 经度
     * 必需
     * >= -180
     * <= 180
     */
    private Double longitude;

    /**
     * 纬度
     * 必需
     * >= -90
     * <= 90
     */
    private Double latitude;
}
