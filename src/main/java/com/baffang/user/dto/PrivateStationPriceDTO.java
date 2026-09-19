package com.baffang.user.dto;

import lombok.Data;

/**
 * 私桩价格设置请求参数
 */
@Data
public class PrivateStationPriceDTO {
    
    /**
     * 固定价格（元/度）
     */
    private Double price;
}