package com.baffang.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * 个人信息返回对象
 */
@Data
public class ProfileVO {
    private Integer id;
    private String name;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private String signature;
    private Integer type;
    private Integer authChargingStation;
    private Integer authVehicle;
    private Integer points;
    private Double balance;
    private Date createTime;
}