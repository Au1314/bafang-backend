package com.baffang.admin.service;

import com.baffang.admin.dto.AuditRecordQueryDTO;
import com.baffang.admin.vo.AuditLogVO;
import com.baffang.admin.vo.AuditRecordDetailVO;
import com.baffang.admin.vo.AuditRecordListVO;
import com.baffang.admin.vo.AuditRecordStatisticsVO;
import com.github.pagehelper.Page;

import java.util.List;

public interface AuditRecordViewService {

    /**
     * 分页查询审核记录列表
     */
    Page<AuditRecordListVO> getList(AuditRecordQueryDTO queryDTO);

    /**
     * 获取审核记录详情
     */
    AuditRecordDetailVO getDetail(Integer id);

    /**
     * 获取审核日志
     */
    List<AuditLogVO> getAuditLogs(Integer id);

    /**
     * 获取统计数据
     */
    AuditRecordStatisticsVO getStatistics();
}
