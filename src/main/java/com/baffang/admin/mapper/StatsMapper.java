package com.baffang.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 统计数据 Mapper
 */
@Mapper
public interface StatsMapper {

    // ==================== 平台统计 ====================
    
    /** 用户总数 */
    Integer countTotalUsers();
    
    /** 今日新增用户 */
    Integer countTodayNewUsers();
    
    /** 充电站总数（私桩） */
    Integer countTotalStations();
    
    /** 充电桩总数（公桩） */
    Integer countTotalPiles();
    
    /** 订单总数 */
    Integer countTotalOrders();
    
    /** 今日订单数 */
    Integer countTodayOrders();
    
    /** 总收入 */
    Double sumTotalRevenue();
    
    /** 今日收入 */
    Double sumTodayRevenue();
    
    /** 总充电量(kWh) */
    Double sumTotalChargeAmount();
    
    /** 今日充电量 */
    Double sumTodayChargeAmount();

    // ==================== 充电站分布 ====================
    
    /** 按认证状态统计私桩 */
    List<Map<String, Object>> countStationsByRegion();
    
    /** 按状态统计私桩 */
    List<Map<String, Object>> countStationsByStatus();
    
    /** 按类型统计私桩 */
    List<Map<String, Object>> countStationsByOperator();

    // ==================== 车辆认证统计 ====================
    
    /** 按状态统计车辆认证 */
    List<Map<String, Object>> countVehicleAuditByStatus();
    
    /** 今日处理的车辆认证数 */
    Integer countTodayProcessedVehicleAudit();
    
    /** 车辆认证平均处理时长(小时) */
    Double avgVehicleAuditProcessTime();

    // ==================== 投诉/举报统计 ====================
    
    /** 按状态统计投诉 */
    List<Map<String, Object>> countComplaintByStatus();
    
    /** 按类型统计投诉/举报 */
    List<Map<String, Object>> countComplaintByType();
    
    /** 今日新增投诉 */
    Integer countTodayNewComplaints();
    
    /** 投诉平均处理时长(小时) */
    Double avgComplaintProcessTime();

    // ==================== 审核记录统计 ====================
    
    /** 按类型和状态统计审核记录 */
    List<Map<String, Object>> countAuditRecordByTypeAndStatus();
    
    /** 待处理审核数(所有类型) */
    Integer countPendingAuditRecords();

    // ==================== 论坛统计 ====================
    
    /** 帖子总数 */
    Integer countTotalPosts();
    
    /** 今日新帖数 */
    Integer countTodayPosts();
    
    /** 评论总数 */
    Integer countTotalComments();
    
    /** 今日评论数 */
    Integer countTodayComments();
    
    /** 帖子分类统计 */
    List<Map<String, Object>> countPostsByCategory();

    // ==================== 紧急呼叫统计 ====================
    
    /** 按状态统计紧急呼叫 */
    List<Map<String, Object>> countEmergencyByStatus();
    
    /** 今日紧急呼叫数 */
    Integer countTodayEmergencyCalls();

    /**
     * 实时充电功率（瓦）：当前处于"已充电(status=2)"状态私桩的额定功率合计
     * （真实可运营整改：大屏实时功率指标的真实数据来源）
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT COALESCE(SUM(max_charge_power), 0) FROM private_charge_station " +
            "WHERE status = 2 AND is_deleted = 0 AND max_charge_power IS NOT NULL")
    Double sumRealtimePower();
    
    /** 平均响应时间(分钟) */
    Double avgEmergencyResponseTime();
    
    /** 按类型统计紧急呼叫 */
    Double avgEmergencyHandleTime();

    // ==================== 客服统计 ====================
    
    /** 今日会话数 */
    Integer countTodayCsSessions();
    
    /** 待处理会话数 */
    Integer countPendingCsSessions();
    
    /** 今日消息数 */
    Integer countTodayCsMessages();
    
    /** 平均响应时间(秒) */
    Double avgCsResponseTime();
    
    /** 客服工作量统计 */
    List<Map<String, Object>> countCsWorkloadByAdmin();
    
    // ==================== 额外统计 ====================
    
    /** V2V呼叫按状态统计 */
    List<Map<String, Object>> countV2VByStatus();
    
    /** 今日V2V呼叫数 */
    Integer countTodayV2VCalls();
    
    /** 订单类型分布 */
    List<Map<String, Object>> countOrdersByType();
    
    /** 订单状态分布 */
    List<Map<String, Object>> countOrdersByStatus();

    /**
     * 导出用：按类型与创建时间范围查询订单明细（不分页；order 为主表需反引号）
     */
    @Select("<script>" +
            "SELECT order_no AS orderNo, user_id AS userId, order_type AS orderType, status, " +
            "electricity, unit_price AS unitPrice, total_amount AS totalAmount, " +
            "actual_amount AS actualAmount, pay_method AS payMethod, " +
            "DATE_FORMAT(create_time, '%Y-%m-%d %H:%i:%s') AS createTime " +
            "FROM `order` WHERE is_deleted = 0 " +
            "<if test='orderType != null'> AND order_type = #{orderType}</if> " +
            "<if test='startDate != null and startDate != \"\"'> AND create_time &gt;= #{startDate}</if> " +
            "<if test='endDate != null and endDate != \"\"'> AND create_time &lt;= CONCAT(#{endDate}, ' 23:59:59')</if> " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Map<String, Object>> selectOrdersForExport(@Param("orderType") Integer orderType,
                                                    @Param("startDate") String startDate,
                                                    @Param("endDate") String endDate);
    
    /** 私桩预约统计 */
    List<Map<String, Object>> countReserveByStatus();
    
    /** 充电桩评分统计 */
    List<Map<String, Object>> getChargeRateStats();
    
    /** 管理员类型统计 */
    List<Map<String, Object>> countAdminsByType();
}
