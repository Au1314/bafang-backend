package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 紧急呼叫记录查询结果VO
 */
@Data
public class EmergencyCallListVO {
    /**
     * 呼叫ID
     */
    private Integer callId;

    /**
     * 呼叫类型：0 = 拖车，1 = 充电桩，2 = 其他
     */
    private Integer callType;

    /**
     * 呼叫时间
     */
    private Date callTime;

    /**
     * 状态：0 = 待响应，1 = 已响应，2 = 已完成，3 = 已取消
     */
    private Integer status;

    /**
     * 响应管理员ID
     */
    private Integer responserId;

    /**
     * 响应管理员名称
     */
    private String responserName;

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
