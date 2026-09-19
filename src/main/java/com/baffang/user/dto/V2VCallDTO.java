package com.baffang.user.dto;

import lombok.Data;

/**
 * V2V充电呼叫请求DTO
 */
@Data
public class V2VCallDTO {
    /**
     * 呼叫范围（公里），最大支持10公里
     * >= 0
     * <= 10
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
