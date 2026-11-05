package com.baffang.admin.service;

import com.baffang.admin.vo.PlatformStatsVO;
import com.baffang.admin.vo.ScreenStatsVO;
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

    /**
     * 获取数据大屏汇总统计（真实数据，一次请求覆盖指标卡所需字段）
     */
    ScreenStatsVO getScreenStats();
}
