package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 响应者查询范围内待响应呼叫结果VO
 */
@Data
public class CallResponseVO {

    /**
     * 呼叫ID
     */
    private Integer id;

    /**
     * 呼叫用户ID
     */
    private Integer requestUserId;

    /**
     * 呼叫用户名称
     */
    private String requestUserName;

    /**
     * 呼叫时间
     */
    private Date callTime;

    /**
     * 呼叫位置
     */
    private String callPosition;

    /**
     * 呼叫位置经度
     */
    private Double callLongitude;

    /**
     * 呼叫位置纬度
     */
    private Double callLatitude;
}