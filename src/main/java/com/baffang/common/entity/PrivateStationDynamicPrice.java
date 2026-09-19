package com.baffang.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 私桩动态价格表实体类
 */
@Data
@TableName("private_station_dynamic_price")
public class PrivateStationDynamicPrice {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 私桩ID
     */
    private Integer stationId;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 充电价格，单位：元/度
     */
    private BigDecimal price;

    /**
     * 逻辑删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;
}