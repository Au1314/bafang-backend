package com.baffang.user.vo;

import lombok.Data;

/**
 * 私桩预约响应VO
 */
@Data
public class PrivateStationReserveVO {
    /**
     * 预约ID
     */
    private Integer reserveId;

    /**
     * 预约状态：0=待确认
     */
    private Integer status;
}