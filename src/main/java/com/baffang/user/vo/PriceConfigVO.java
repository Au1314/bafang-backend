package com.baffang.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 私桩价格配置查询返回参数
 */
@Data
public class PriceConfigVO {

    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 价格类型：0-静态价格，1-动态价格
     */
    private Integer priceType;

    /**
     * 固定价格（元/度）
     */
    private Double fixedPrice;

    /**
     * 动态价格规则列表
     */
    private List<DynamicRuleVO> dynamicRules;

    /**
     * 动态价格规则项
     */
    @Data
    public static class DynamicRuleVO {

        /**
         * 规则ID
         */
        private Integer ruleId;

        /**
         * 开始时间（格式：HH:mm:ss）
         */
        private String startTime;

        /**
         * 结束时间（格式：HH:mm:ss）
         */
        private String endTime;

        /**
         * 充电价格，单位：元/度
         */
        private Double price;
    }
}