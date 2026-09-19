package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 私桩桩主查看的预约记录VO
 */
@Data
public class PrivateStationOwnerReserveListVO {
    /**
     * 预约ID
     */
    private Integer reserveId;

    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 私桩名称
     */
    private String stationName;

    /**
     * 预约用户ID
     */
    private Integer reserveUserId;

    /**
     * 预约用户姓名
     */
    private String reserveUserName;

    /**
     * 预约时间
     */
    private Date reserveTime;

    /**
     * 预约状态：0=待确认，1=已确认，2=已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}