package com.baffang.admin.service;

import com.baffang.admin.dto.ComplaintAuditDTO;
import com.baffang.admin.dto.ComplaintQueryDTO;
import com.baffang.admin.vo.ComplaintDetailVO;
import com.baffang.admin.vo.ComplaintListVO;
import com.baffang.admin.vo.ComplaintStatisticsVO;
import com.github.pagehelper.Page;

public interface ComplaintAuditService {
    
    Page<ComplaintListVO> getComplaintList(ComplaintQueryDTO query);
    
    ComplaintDetailVO getComplaintDetail(Integer id);
    
    void auditComplaint(Integer id, ComplaintAuditDTO dto, Integer adminId);
    
    ComplaintStatisticsVO getStatistics();
}
