package com.baffang.user.vo;

import lombok.Data;

/**
 * 导航私桩搜索结果VO
 */
@Data
public class NaviPrivateStationVO {

    /**
     * 私桩ID
     */
    private Integer id;

    /**
     * 私桩名称
     */
    private String name;

    /**
     * 私桩位置名称
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
     * 最大车辆数
     */
    private Integer maxVehicles;

    /**
     * 当前车辆数
     */
    private Integer currentVehicles;

    /**
     * 到当前用户位置的距离（米）
     */
    private Double distance;

    /**
     * 私桩图片
     */
    private String image;

    /**
     * 私桩价格
     */
    private Double price;
}