package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserDetailVO {
    private Integer id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private String signature;
    
    private Integer type;
    private String typeName;
    
    private Integer authChargingStation;
    private String authChargingStationName;
    
    private Integer authVehicle;
    private String authVehicleName;
    
    private Integer privacyVehicle;
    private String privacyVehicleName;
    
    private Integer privacyStation;
    private String privacyStationName;
    
    private Integer points;
    private BigDecimal balance;
    
    private Integer isDeleted;
    private String statusName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    // 扩展信息
    private Integer stationCount;    // 拥有的私桩数量
    private Integer vehicleCount;    // 绑定的车辆数量
    private Integer orderCount;      // 订单数量
}
