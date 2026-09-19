package com.baffang.user.dto;

import lombok.Data;

/**
 * 充电桩搜索结果DTO
 */
@Data
public class StationSearchResultDTO {
    /**
     * 充电桩ID
     */
    private Integer id;

    /**
     * 充电桩名称
     */
    private String name;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 充电价格，单位：元/度
     */
    private Double price;

    /**
     * 开放状态：0：关闭，1：开启
     */
    private Integer openStatus;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 到中心点的距离（米）
     */
    private Double distance;

    /**
     * 充电桩类型：0：私桩，1：公桩
     */
    private Integer stationType;
}