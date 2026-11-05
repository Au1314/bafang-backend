package com.baffang.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据大屏汇总统计 VO
 * 一次请求返回大屏核心指标所需的真实数据；
 * 实时充电功率 / 设备在线率无数据来源，仍由前端以演示数据展示并明示标注。
 */
@Data
public class ScreenStatsVO {

    /** 用户总数 */
    private Integer totalUsers;

    /** 今日活跃用户数 */
    private Integer dailyActiveUsers;

    /** 订单总数 */
    private Integer totalOrders;

    /** 今日订单数 */
    private Integer todayOrders;

    /** 总收入（元） */
    private Double totalRevenue;

    /** 今日收入（元） */
    private Double todayRevenue;

    /** 总充电量（kWh） */
    private Double totalChargeAmount;

    /** 今日充电量（kWh） */
    private Double todayChargeAmount;

    /** 故障私桩数量（status=3） */
    private Integer faultPileCount;

    /** 今日紧急呼叫数 */
    private Integer todayEmergencyCalls;

    /** 待接入客服会话数 */
    private Integer pendingConversations;

    /** 私桩状态分布（用于大屏图表） */
    private List<Map<String, Object>> stationStatusDistribution;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date statsDate;
}
