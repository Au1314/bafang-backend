package com.baffang.admin.service;

import org.springframework.http.ResponseEntity;

/**
 * 数据导出服务（CSV）
 */
public interface DataExportService {

    /**
     * 导出审核记录 CSV
     *
     * @param params {dateRange:[start,end], auditType}
     */
    ResponseEntity<byte[]> exportAuditRecords(java.util.Map<String, Object> params);

    /**
     * 导出用户数据 CSV
     */
    ResponseEntity<byte[]> exportUsers();

    /**
     * 导出订单明细 CSV
     *
     * @param params {dateRange:[start,end], orderType}
     */
    ResponseEntity<byte[]> exportOrders(java.util.Map<String, Object> params);
}
