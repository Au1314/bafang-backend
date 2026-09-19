package com.baffang.admin.service;

import com.baffang.admin.dto.VehicleAuditQueryDTO;
import com.baffang.admin.dto.VehicleAuditReviewDTO;
import com.baffang.admin.vo.VehicleAuditDetailVO;
import com.baffang.admin.vo.VehicleAuditListVO;
import com.baffang.admin.vo.VehicleAuditStatisticsVO;
import com.github.pagehelper.Page;

public interface VehicleAuditService {
    
    /**
     * 查询审核列表
     */
    Page<VehicleAuditListVO> getAuditList(VehicleAuditQueryDTO query);
    
    /**
     * 查询审核详情
     */
    VehicleAuditDetailVO getAuditDetail(Integer id);
    
    /**
     * 认领审核任务
     */
    void claimAudit(Integer id, Integer adminId);
    
    /**
     * 提交审核结果
     */
    void submitReview(VehicleAuditReviewDTO dto, Integer adminId);
    
    
    /**
     * 获取审核统计
     */
    VehicleAuditStatisticsVO getStatistics();
}
