package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserListVO {
    private Integer id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private Integer type;
    private String typeName;
    private Integer authChargingStation;
    private String authChargingStationName;
    private Integer authVehicle;
    private String authVehicleName;
    private Integer points;
    private BigDecimal balance;
    private Integer isDeleted;
    private String statusName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
