package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * V2V呼叫表实体类
 */
@Data
public class VToV {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 呼叫用户 id(关联用户表 id)
     */
    private Integer requestUserId;

    /**
     * 响应用户 id(关联用户表 id)
     */
    private Integer responseUserId;

    /**
     * 呼叫范围，单位：公里
     */
    private Double callRange;

    /**
     * 呼叫时间
     */
    private Date callTime;

    /**
     * 呼叫位置
     */
    private String callPosition;

    /**
     * 目标位置(两位用户协商后确定的位置)
     */
    private String targetPosition;

    /**
     * 呼叫位置经度（-180~180）
     */
    private Double callLongitude;

    /**
     * 呼叫位置纬度(-90~90）
     */
    private Double callLatitude;

    /**
     * 目标位置经度（-180~180）
     */
    private Double targetLongitude;

    /**
     * 目标位置纬度(-90~90）
     */
    private Double targetLatitude;

    /**
     * 状态：0：待响应，1：已响应，2：已完成，3：已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}