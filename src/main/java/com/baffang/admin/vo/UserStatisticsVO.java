package com.baffang.admin.vo;

import lombok.Data;

@Data
public class UserStatisticsVO {
    private Integer total;           // 用户总数
    private Integer normalCount;     // 正常用户数
    private Integer disabledCount;   // 禁用用户数
    private Integer todayNew;        // 今日新增
    private Integer weekNew;         // 本周新增
    private Integer monthNew;        // 本月新增
    private Integer vipCount;        // VIP用户数
    private Integer authStationCount; // 已认证充电桩用户数
    private Integer authVehicleCount; // 已认证车辆用户数
}
