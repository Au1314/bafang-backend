package com.baffang.admin.mapper;

import com.baffang.admin.dto.EmergencyCallPendingQueryDTO;
import com.baffang.admin.dto.EmergencyCallQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmergencyCallMapper {

    /**
     * 查询待响应呼叫列表（按距离排序）
     * 使用 Haversine 公式计算直线距离
     */
    List<Map<String, Object>> selectPendingListByDistance(@Param("query") EmergencyCallPendingQueryDTO query);

    /**
     * 分页查询呼叫列表
     */
    List<Map<String, Object>> selectList(@Param("query") EmergencyCallQueryDTO query);

    /**
     * 查询呼叫详情
     */
    Map<String, Object> selectDetailById(@Param("id") Integer id);

    /**
     * 检查呼叫是否存在
     */
    boolean existsById(@Param("id") Integer id);

    /**
     * 获取呼叫状态
     */
    Integer selectStatusById(@Param("id") Integer id);

    /**
     * 获取呼叫的响应管理员ID
     */
    Integer selectResponseAdminId(@Param("id") Integer id);

    /**
     * 响应呼叫
     */
    int respond(@Param("id") Integer id, @Param("adminId") Integer adminId);

    /**
     * 更新目标位置
     */
    int updateTarget(@Param("id") Integer id,
                     @Param("targetPosition") String targetPosition,
                     @Param("targetLongitude") Double targetLongitude,
                     @Param("targetLatitude") Double targetLatitude);

    /**
     * 完成呼叫
     */
    int complete(@Param("id") Integer id);

    /**
     * 取消呼叫
     */
    int cancel(@Param("id") Integer id);

    /**
     * 检查管理员是否有正在处理的呼叫
     */
    boolean hasActiveCall(@Param("adminId") Integer adminId);

    /**
     * 查询我的响应记录
     */
    List<Map<String, Object>> selectMyResponses(@Param("adminId") Integer adminId,
                                                  @Param("status") Integer status,
                                                  @Param("startTime") String startTime,
                                                  @Param("endTime") String endTime);

    /**
     * 查询统计数据
     */
    Map<String, Object> selectStatistics();

    /**
     * 查询个人统计数据
     */
    Map<String, Object> selectMyStatistics(@Param("adminId") Integer adminId);


     /**
     * 查询呼叫用户ID（用于发送通知）
     */
     Integer selectRequestUserId(@Param("id") Integer id);
}
