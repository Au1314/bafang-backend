package com.baffang.user.vo;

import lombok.Data;

/**
 * 处理私桩预约申请响应VO
 */
@Data
public class HandlePrivateStationReserveVO {
    /**
     * 预约ID
     */
    private Integer reserveId;

    /**
     * 处理后的状态
     */
    private Integer status;

    /**
     * 更新时间
     */
    private String updateTime;
}