package com.baffang.user.dto;

import lombok.Data;

/**
 * 更新隐私设置请求参数
 */
@Data
public class UpdatePrivacySettingsDTO {
    /**
     * 允许他人查看我的车辆
     * 1=允许，0=不允许
     */
    private Integer showVehicle;
    
    /**
     * 允许他人查看我的充电桩
     * 1=允许，0=不允许
     */
    private Integer showChargingStation;
}