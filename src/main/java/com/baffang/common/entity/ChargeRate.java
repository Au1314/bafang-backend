package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

/**
 * 充电桩评价表实体
 */
@Data
public class ChargeRate {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 评价用户id(关联用户表id)
     */
    private Integer userId;

    /**
     * 0：公桩评价，1：私桩评价
     */
    private Integer type;

    /**
     * 评价充电桩id
     */
    private Integer chargingStationId;

    /**
     * 是否匿名 0：否，1：是
     */
    private Integer anonymous;

    /**
     * 评价分数，1-5分
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 0：正常，1：已删除
     */
    private Integer isDeleted;
}