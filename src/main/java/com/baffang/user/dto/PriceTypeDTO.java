package com.baffang.user.dto;

import lombok.Data;

/**
 * 私桩价格类型设置请求参数
 */
@Data
public class PriceTypeDTO {

    /**
     * 价格类型：0-静态价格，1-动态价格
     */
    private Integer priceType;
}