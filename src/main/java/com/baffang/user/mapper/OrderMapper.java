package com.baffang.user.mapper;

import com.baffang.common.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 订单 Mapper（契约补齐：order 表此前只有 DDL 无任何代码）
 * 注意：order 是 MySQL 保留字，SQL 中必须用反引号
 */
@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order` (order_no, user_id, order_type, charging_station_id, charging_station_type, " +
            "v2v_call_id, electricity, unit_price, electricity_fee, service_fee, discount_amount, " +
            "total_amount, actual_amount, status, remark, create_time, update_time, is_deleted) " +
            "VALUES (#{orderNo}, #{userId}, #{orderType}, #{chargingStationId}, #{chargingStationType}, " +
            "#{v2vCallId}, #{electricity}, #{unitPrice}, #{electricityFee}, #{serviceFee}, #{discountAmount}, " +
            "#{totalAmount}, #{actualAmount}, #{status}, #{remark}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT id, order_no AS orderNo, user_id AS userId, order_type AS orderType, " +
            "charging_station_id AS chargingStationId, charging_station_type AS chargingStationType, " +
            "v2v_call_id AS v2vCallId, start_time AS startTime, end_time AS endTime, duration, " +
            "electricity, unit_price AS unitPrice, electricity_fee AS electricityFee, " +
            "service_fee AS serviceFee, discount_amount AS discountAmount, " +
            "total_amount AS totalAmount, actual_amount AS actualAmount, status, " +
            "pay_time AS payTime, pay_method AS payMethod, remark, " +
            "create_time AS createTime, update_time AS updateTime " +
            "FROM `order` WHERE id = #{id} AND is_deleted = 0")
    Order selectById(@Param("id") Integer id);

    /** 开始充电：待支付(0) → 充电中(1)，乐观条件防并发重复开始 */
    @Update("UPDATE `order` SET status = 1, start_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id} AND status = 0 AND is_deleted = 0")
    int startCharging(@Param("id") Integer id);

    /** 完成充电：充电中(1) → 充电完成待支付(2) */
    @Update("UPDATE `order` SET status = 2, end_time = NOW(), " +
            "duration = #{duration}, electricity = #{electricity}, " +
            "unit_price = #{unitPrice}, electricity_fee = #{electricityFee}, " +
            "total_amount = #{totalAmount}, actual_amount = #{totalAmount}, update_time = NOW() " +
            "WHERE id = #{id} AND status = 1 AND is_deleted = 0")
    int finishCharging(@Param("id") Integer id, @Param("duration") Integer duration,
                       @Param("electricity") java.math.BigDecimal electricity,
                       @Param("unitPrice") java.math.BigDecimal unitPrice,
                       @Param("electricityFee") java.math.BigDecimal electricityFee,
                       @Param("totalAmount") java.math.BigDecimal totalAmount);

    /** 支付：待支付(0)或完成待支付(2) → 已完成(3)，乐观条件防重复支付 */
    @Update("UPDATE `order` SET status = 3, pay_time = NOW(), pay_method = #{payMethod}, " +
            "actual_amount = #{actualAmount}, update_time = NOW() " +
            "WHERE id = #{id} AND status IN (0, 2) AND is_deleted = 0")
    int pay(@Param("id") Integer id, @Param("payMethod") Integer payMethod,
            @Param("actualAmount") java.math.BigDecimal actualAmount);

    /** 取消：待支付(0)或完成待支付(2) → 已取消(4) */
    @Update("UPDATE `order` SET status = 4, update_time = NOW() " +
            "WHERE id = #{id} AND status IN (0, 2) AND is_deleted = 0")
    int cancel(@Param("id") Integer id);

    /** 我的订单列表（管理端导出/统计复用） */
    @Select("<script>" +
            "SELECT id, order_no AS orderNo, user_id AS userId, order_type AS orderType, " +
            "charging_station_id AS chargingStationId, charging_station_type AS chargingStationType, " +
            "start_time AS startTime, end_time AS endTime, duration, electricity, " +
            "unit_price AS unitPrice, total_amount AS totalAmount, actual_amount AS actualAmount, " +
            "status, pay_method AS payMethod, create_time AS createTime " +
            "FROM `order` WHERE is_deleted = 0 " +
            "<if test='userId != null'> AND user_id = #{userId}</if> " +
            "<if test='status != null'> AND status = #{status}</if> " +
            "<if test='orderType != null'> AND order_type = #{orderType}</if> " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<Map<String, Object>> selectList(@Param("userId") Integer userId,
                                         @Param("status") Integer status,
                                         @Param("orderType") Integer orderType);
}
