package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 私桩预约表实体类
 */
@Data
public class PrivateStationReserve {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 预约用户 id(关联用户表 id)
     */
    private Integer userId;

    /**
     * 私桩所有者 id(关联用户表 id)
     */
    private Integer stationOwnerId;

    /**
     * 预约私桩 id(关联私桩表 id)
     */
    private Integer chargingStationId;

    /**
     * 预约时间
     */
    private Date reserveTime;

    /**
     * 预约状态：0：待确认，1：已确认，2：已取消
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