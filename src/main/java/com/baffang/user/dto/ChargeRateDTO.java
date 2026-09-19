package com.baffang.user.dto;

import lombok.Data;

/**
 * 充电桩评论发布DTO
 */
@Data
public class ChargeRateDTO {
    /**
     * 充电桩ID
     */
    private String stationId;
    
    /**
     * 桩类型：0 = 公桩，1 = 私桩
     */
    private String stationType;
    
    /**
     * 评分（1-5 分）
     */
    private String score;
    
    /**
     * 评论内容
     */
    private String comment;
    
    /**
     * 是否匿名：0 = 否，1 = 是
     */
    private String anonymous = "0";
}