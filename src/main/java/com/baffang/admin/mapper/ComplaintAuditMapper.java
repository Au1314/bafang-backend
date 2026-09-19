package com.baffang.admin.mapper;

import com.baffang.admin.dto.ComplaintQueryDTO;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.ComplaintDetailVO;
import com.baffang.admin.vo.ComplaintListVO;
import com.baffang.admin.vo.ComplaintStatisticsVO;
import com.baffang.common.entity.AuditLog;
import com.baffang.common.entity.AuditRecord;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ComplaintAuditMapper {
    
    Page<ComplaintListVO> selectComplaintList(@Param("query") ComplaintQueryDTO query);
    
    ComplaintDetailVO selectComplaintDetail(@Param("id") Integer id);
    
    AuditRecord selectById(@Param("id") Integer id);
    
    int updateRecord(AuditRecord record);
    
    int insertAuditLog(AuditLog log);
    
    List<AuditLogVO> selectAuditLogs(@Param("auditId") Integer auditId);
    
    ComplaintStatisticsVO selectStatistics();
}
