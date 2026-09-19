package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 私桩预约记录VO
 */
@Data
public class PrivateStationReserveListVO {
    /**
     * 预约ID
     */
    private Integer reserveId;

    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 私桩所有者ID
     */
    private Integer stationOwnerId;

    /**
     * 私桩名称
     */
    private String stationName;

    /**
     * 桩主姓名
     */
    private String ownerName;

    /**
     * 预约时间
     */
    private Date reserveTime;

    /**
     * 预约状态：0=待确认，1=已确认，2=已取消
     */
    private Integer reserveStatus;

    /**
     * 创建时间
     */
    private Date createTime;
}