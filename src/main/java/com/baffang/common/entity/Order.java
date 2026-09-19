package com.baffang.common.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表实体类
 */
@Data
public class Order {
    /**
     * 主键（自增）
     */
    private Integer id;

    /**
     * 订单编号（非空）
     */
    private String orderNo;

    /**
     * 用户 ID（关联用户表 id，非空）
     */
    private Integer userId;

    /**
     * 订单类型：0 - 公桩充电，1 - 私桩充电，2-V2V 充电（非空）
     */
    private Integer orderType;

    /**
     * 充电桩 ID（可为空）
     */
    private Integer chargingStationId;

    /**
     * 充电桩类型：0 - 公桩，1 - 私桩（可为空）
     */
    private Integer chargingStationType;

    /**
     * V2V 呼叫 ID（关联 V2V 呼叫表 id，可为空）
     */
    private Integer v2vCallId;

    /**
     * 充电开始时间（可为空）
     */
    private Date startTime;

    /**
     * 充电结束时间（可为空）
     */
    private Date endTime;

    /**
     * 充电时长（分钟，可为空）
     */
    private Integer duration;

    /**
     * 充电电量（度，可为空）
     */
    private BigDecimal electricity;

    /**
     * 单价（元 / 度，可为空）
     */
    private BigDecimal unitPrice;

    /**
     * 电费（元，可为空）
     */
    private BigDecimal electricityFee;

    /**
     * 服务费（元，可为空）
     */
    private BigDecimal serviceFee;

    /**
     * 优惠金额（元，默认值 0）
     */
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 订单总金额（元，非空）
     */
    private BigDecimal totalAmount;

    /**
     * 实际支付金额（元，非空）
     */
    private BigDecimal actualAmount;

    /**
     * 订单状态：0 - 待支付，1 - 充电中，2 - 充电完成待支付，3 - 已完成，4 - 已取消，5 - 已退款（默认 0）
     */
    private Integer status = 0;

    /**
     * 支付时间（可为空）
     */
    private Date payTime;

    /**
     * 支付方式：0 - 微信，1 - 支付宝，2 - 余额，3 - 积分抵扣（可为空）
     */
    private Integer payMethod;

    /**
     * 备注（可为空）
     */
    private String remark;

    /**
     * 创建时间（默认当前时间）
     */
    private Date createTime;

    /**
     * 更新时间（默认当前时间，更新时自动刷新）
     */
    private Date updateTime;

    /**
     * 逻辑删除标识：0 - 正常，1 - 已删除（默认 0）
     */
    private Integer isDeleted = 0;
}