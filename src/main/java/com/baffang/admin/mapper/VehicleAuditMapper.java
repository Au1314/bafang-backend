package com.baffang.admin.mapper;

import com.baffang.admin.dto.VehicleAuditQueryDTO;
import com.baffang.admin.vo.VehicleAuditListVO;
import com.baffang.admin.vo.VehicleAuditStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VehicleAuditMapper {
    
    /**
     * 查询车主认证审核列表
     */
    List<VehicleAuditListVO> selectAuditList(@Param("query") VehicleAuditQueryDTO query);
    
    /**
     * 查询审核详情
     */
    Map<String, Object> selectAuditDetail(@Param("id") Integer id);
    
    /**
     * 查询车辆信息
     */
    Map<String, Object> selectVehicleInfo(@Param("id") Integer id);
    
    /**
     * 查询申请人信息
     */
    Map<String, Object> selectApplicantInfo(@Param("id") Integer id);
    
    /**
     * 查询审核日志
     */
    List<Map<String, Object>> selectAuditLogs(@Param("auditId") Integer auditId);
    
    /**
     * 更新车辆认证状态
     */
    int updateCarAuthStatus(@Param("id") Integer id, @Param("authStatus") Integer authStatus);
    
    /**
     * 更新用户认证车辆状态
     */
    int updateUserAuthVehicle(@Param("userId") Integer userId, @Param("authVehicle") Integer authVehicle);
    
    /**
     * 统计各状态数量
     */
    VehicleAuditStatisticsVO selectStatistics();
    
    /**
     * 统计今日处理数量
     */
    int selectTodayProcessedCount();
}
