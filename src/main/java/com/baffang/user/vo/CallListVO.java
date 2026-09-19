package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * V2V呼叫记录查询结果VO
 */
@Data
public class CallListVO {
    /**
     * 呼叫ID
     */
    private Integer callId;

    /**
     * 角色：0 = 发起者，1 = 响应者
     */
    private Integer role;

    /**
     * 呼叫范围（公里）
     */
    private Integer callRange;

    /**
     * 呼叫时间
     */
    private Date callTime;

    /**
     * 状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消
     */
    private Integer status;

    /**
     * 呼叫用户ID
     */
    private Integer callUserId;

    /**
     * 呼叫用户名称
     */
    private String callUserName;

    /**
     * 响应用户ID
     */
    private Integer responseUserId;

    /**
     * 响应用户名称
     */
    private String responseUserName;

    /**
     * 呼叫位置
     */
    private String callPosition;

    /**
     * 目标位置
     */
    private String targetPosition;

    /**
     * 目标经度
     */
    private Double targetLongitude;

    /**
     * 目标纬度
     */
    private Double targetLatitude;

    /**
     * 呼叫位置经度
     */
    private Double callLongitude;

    /**
     * 呼叫位置纬度
     */
    private Double callLatitude;
}