package com.baffang.user.vo;

import lombok.Data;

/**
 * 私桩列表查询VO
 */
@Data
public class PrivateStationListVO {

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
     * 认证状态
     */
    private Integer authStatus;

    /**
     * 开放状态
     */
    private Integer openStatus;

    /**
     * 评分
     */
    private Double rating;
}