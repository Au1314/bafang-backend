package com.baffang.admin.vo;

import lombok.Data;

@Data
public class PlatformStatsVO {
    
    /**
     * 用户总数
     */
    private Integer totalUsers;
    
    /**
     * 今日活跃用户数（今日登录过的用户）
     */
    private Integer dailyActiveUsers;


        /**
     * 已认证私桩数量
     */
        private Integer authenticatedStations;
    
        /**
         * 已认证车主数量
         */
        private Integer authenticatedCars;
    
    /**
     * 统计日期
     */
    private String statsDate;
}
