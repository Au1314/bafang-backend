package com.baffang.admin.mapper;

import com.baffang.admin.dto.AuditRecordQueryDTO;
import com.baffang.common.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditRecordMapper {
    
    /**
     * 插入审核记录
     */
    int insert(AuditRecord record);
    
    /**
     * 根据ID查询
     */
    AuditRecord selectById(@Param("id") Integer id);
    
    /**
     * 根据审核单号查询
     */
    AuditRecord selectByAuditNo(@Param("auditNo") String auditNo);
    
    /**
     * 根据业务ID和类型查询
     */
    AuditRecord selectByBusinessIdAndType(@Param("businessId") Integer businessId, 
                                          @Param("auditType") Integer auditType);
    
    /**
     * 更新审核记录
     */
    int updateById(AuditRecord record);
    
    /**
     * 更新审核状态
     */
    int updateStatus(@Param("id") Integer id, 
                     @Param("currentStatus") Integer currentStatus,
                     @Param("currentAdminId") Integer currentAdminId);
    
    /**
     * 完成审核（通过/拒绝）
     */
    int finishAudit(@Param("id") Integer id,
                    @Param("currentStatus") Integer currentStatus,
                    @Param("finalOpinion") String finalOpinion,
                    @Param("rejectReason") String rejectReason);
    
    /**
     * 分页查询审核列表
     */
    List<AuditRecord> selectList(@Param("auditType") Integer auditType,
                                  @Param("currentStatus") Integer currentStatus,
                                  @Param("currentAdminId") Integer currentAdminId);

    /**
     * 导出用：按类型与提交时间范围查询审核记录（不分页）
     */
    @Select("<script>" +
            "SELECT id, audit_no AS auditNo, audit_type AS auditType, business_id AS businessId, " +
            "applicant_id AS applicantId, title, current_status AS currentStatus, priority, " +
            "submit_time AS submitTime, finish_time AS finishTime, " +
            "final_opinion AS finalOpinion, reject_reason AS rejectReason " +
            "FROM audit_record WHERE is_deleted = 0 " +
            "<if test='auditType != null'> AND audit_type = #{auditType}</if> " +
            "<if test='startDate != null and startDate != \"\"'> AND submit_time &gt;= #{startDate}</if> " +
            "<if test='endDate != null and endDate != \"\"'> AND submit_time &lt;= CONCAT(#{endDate}, ' 23:59:59')</if> " +
            "ORDER BY submit_time DESC" +
            "</script>")
    List<AuditRecord> selectForExport(@Param("auditType") Integer auditType,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate);
    
    /**
     * 根据申请人ID查询
     */
    List<AuditRecord> selectByApplicantId(@Param("applicantId") Integer applicantId,
                                           @Param("auditType") Integer auditType);



    // ========== 新增方法（审核记录查看功能） ==========

/**
 * 分页查询审核记录列表（带申请人信息）
 */
List<Map<String, Object>> selectListWithApplicant(@Param("query") AuditRecordQueryDTO query);

/**
 * 查询审核记录详情（带申请人和处理人信息）
 */
Map<String, Object> selectDetailById(@Param("id") Integer id);

/**
 * 查询总体统计
 */
Map<String, Object> selectOverallStatistics();

/**
 * 查询按类型统计
 */
List<Map<String, Object>> selectTypeStatistics();

}
