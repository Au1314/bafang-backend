package com.baffang.user.vo;

import lombok.Data;

/**
 * 隐私设置返回对象
 */
@Data
public class PrivacySettingsVO {
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