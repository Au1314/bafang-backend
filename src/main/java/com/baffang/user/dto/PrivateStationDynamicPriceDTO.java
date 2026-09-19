package com.baffang.user.dto;

import lombok.Data;

import java.util.List;

/**
 * 私桩动态价格设置请求参数
 */
@Data
public class PrivateStationDynamicPriceDTO {

    /**
     * 动态价格规则列表
     */
    private List<DynamicPriceRuleDTO> rules;

    /**
     * 动态价格规则项
     */
    @Data
    public static class DynamicPriceRuleDTO {

        /**
         * 时段开始时间（格式：HH:mm:ss）
         */
        private String beginTime;

        /**
         * 时段结束时间（格式：HH:mm:ss）
         */
        private String endTime;

        /**
         * 充电价格，单位：元/度
         */
        private Double price;
    }
}