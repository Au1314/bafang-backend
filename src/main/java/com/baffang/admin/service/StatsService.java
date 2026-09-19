package com.baffang.admin.service;

import com.baffang.admin.vo.PlatformStatsVO;
import com.baffang.admin.vo.StationDistributionVO;

public interface StatsService {

    /**
     * 获取平台统计数据
     */
    PlatformStatsVO getPlatformStats();

        /**
     * 获取私桩分布数据
     */
    StationDistributionVO getStationDistribution();
}
