package com.baffang.user.vo;

import lombok.Data;

/**
 * 充电桩评论VO类
 */
@Data
public class ChargeRateVO {
    /**
     * 评论ID
     */
    private Integer rateId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名称（当匿名时显示"匿名用户"）
     */
    private String username;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 是否匿名：0 = 否，1 = 是
     */
    private Integer anonymous;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 用户头像URL，如果是匿名用户则为null
     */
    private String avatar;
}