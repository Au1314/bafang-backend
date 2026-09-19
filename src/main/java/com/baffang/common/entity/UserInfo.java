package com.baffang.common.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserInfo {
    private Integer id;
    private String username;
    private String name;
    private String password;
    private Integer type;
    private String phone;
    private String email;
    private String avatar;
    private String signature;
    private Integer authChargingStation;
    private Integer authVehicle;
    private Integer privacyVehicle;
    private Integer privacyStation;
    private Integer points;
    private Double balance;
    private Integer isDeleted;
    private Date createTime;
    private Date updateTime;
    private Date lastLoginTime;
}
