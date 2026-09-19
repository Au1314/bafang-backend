package com.baffang.admin.mapper;

import com.baffang.common.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditLogMapper {
    
    /**
     * 插入操作日志
     */
    int insert(AuditLog log);
    
    /**
     * 根据审核ID查询操作日志
     */
    List<AuditLog> selectByAuditId(@Param("auditId") Integer auditId);


    /**
 * 查询审核日志（带操作人信息）
 */
List<Map<String, Object>> selectLogsWithAdmin(@Param("auditId") Integer auditId);




}
